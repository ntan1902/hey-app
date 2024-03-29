package com.hey.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hey.handler.ws.WsHandler;
import com.hey.manager.JwtManager;
import com.hey.manager.UserWsChannelManager;
import com.hey.model.*;
import com.hey.util.LogUtils;
import com.hey.util.PropertiesUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class WsServer {

    private WsHandler wsHandler;

    private UserWsChannelManager userWsChannelManager;

    private JwtManager jwtManager;

    private static final Logger LOGGER = LogManager.getLogger(WsServer.class);

    private WsServer() {
    }

    public static WsServer newInstance() {
        return new WsServer();
    }

    public Future<Void> createWsServer(Vertx vertx) {
        Future future = Future.succeededFuture();
        vertx.createHttpServer().websocketHandler(ws -> {
            final String id = ws.textHandlerID();
            String query = ws.query();
            if (!StringUtils.isBlank(query)) {
                String jwt = query.substring(query.indexOf('=') + 1);
                if (!StringUtils.isBlank(jwt)) {
                    JsonObject authObj = new JsonObject().put("jwtUser", jwt);
                    jwtManager.authenticate(authObj, event -> {
                        if (event.succeeded()) {
                            String userId = event.result().principal().getString("userId");

                            LogUtils.log("User " + userId + " registering new connection with id: " + id);
                            LOGGER.info("registering new connection with id: {} for user: {}", id, userId);

                            userWsChannelManager.registerChannel(userId, id)
                                    .setHandler(ar -> handleNotificationCase(ar, userId, true));

                            ws.closeHandler(event1 -> {
                                LOGGER.info("un-registering connection with id: {} for user: {}", id, userId);
                                userWsChannelManager.removeChannel(userId, id)
                                        .setHandler(ar -> handleNotificationCase(ar, userId, false));
                            });

                            ws.handler(data -> {
                                try {
                                    JsonObject json = new JsonObject(data.toString());
                                    String type = json.getString("type");
                                    ObjectMapper mapper = new ObjectMapper();
                                    switch (type) {
                                        case IWsMessage.TYPE_CHAT_ITEMS_REQUEST:
                                            ChatContainerRequest chatContainerRequest = mapper
                                                    .readValue(data.toString(), ChatContainerRequest.class);
                                            LogUtils.log("User " + userId + " load chat container "
                                                    + chatContainerRequest.getSessionId());
                                            wsHandler.handleChatContainerRequest(chatContainerRequest, id,
                                                    userId);
                                            break;

                                        case IWsMessage.TYPE_CHAT_MESSAGE_REQUEST:
                                            ChatMessageRequest chatMessageRequest = mapper
                                                    .readValue(data.toString(), ChatMessageRequest.class);
                                            if (!"-1".equals(chatMessageRequest.getSessionId()))
                                                LogUtils.log("User " + userId + " send a chat message to "
                                                        + chatMessageRequest.getSessionId());
                                            else
                                                LogUtils.log("User " + userId + " start a chat message to "
                                                        + ArrayUtils
                                                        .toString(chatMessageRequest.getUsernames()));
                                            wsHandler.handleChatMessageRequest(chatMessageRequest, id, userId);
                                            break;
                                    }
                                } catch (IOException e) {
                                    LOGGER.error(data.toString(), e);
                                }
                            });
                        } else {
                            LOGGER.info("Authentication Failed for id: {}", id);
                            ws.reject();
                        }
                    });

                } else {
                    LOGGER.info("Authentication Failed for id: {}", id);
                    ws.reject();
                }
            } else {
                LOGGER.info("Authentication Failed for id: ", id);
                ws.reject();
            }

        }).listen(PropertiesUtils.getInstance().getIntValue("ws.port"));

        return future;
    }

    public void setWsHandler(WsHandler wsHandler) {
        this.wsHandler = wsHandler;
    }

    public void setUserWsChannelManager(UserWsChannelManager userWsChannelManager) {
        this.userWsChannelManager = userWsChannelManager;
    }

    public void setJwtManager(JwtManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    private void handleNotificationCase(AsyncResult<Boolean> ar, String userId, boolean state) {
        if (ar.succeeded()) {
            boolean shouldNotify = ar.result();
            if (shouldNotify) {
                wsHandler.getUserFull(userId).setHandler(ar2 -> {
                    if (ar2.succeeded()) {
                        String fullName = ar2.result().getFullName();
                        wsHandler.getFriendLists(userId).setHandler(ar3 -> {
                            if (ar3.succeeded()) {
                                List<FriendList> friendLists = ar3.result();
                                if (friendLists.size() > 0) {
                                    for (FriendList friendList : friendLists) {
                                        String friendId = friendList.getFriendUserHashes().getUserId();
                                        if (state) {
                                            UserOnlineResponse userOnlineResponse = new UserOnlineResponse();
                                            userOnlineResponse.setType(IWsMessage.TYPE_NOTIFICATION_FRIEND_ONLINE);
                                            userOnlineResponse.setUserId(userId);
                                            userOnlineResponse.setFullName(fullName);
                                            userWsChannelManager.sendMessage(userOnlineResponse, friendId);
                                        } else {
                                            UserOfflineResponse userOfflineResponse = new UserOfflineResponse();
                                            userOfflineResponse.setType(IWsMessage.TYPE_NOTIFICATION_FRIEND_OFFLINE);
                                            userOfflineResponse.setUserId(userId);
                                            userOfflineResponse.setFullName(fullName);
                                            userWsChannelManager.sendMessage(userOfflineResponse, friendId);
                                        }

                                    }
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
