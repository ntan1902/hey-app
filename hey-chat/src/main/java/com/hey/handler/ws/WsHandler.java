package com.hey.handler.ws;

import com.hey.manager.UserWsChannelManager;
import com.hey.model.*;
import com.hey.model.lucky.UserIdSessionIdRequest;
import com.hey.model.lucky.UserIdSessionIdResponse;
import com.hey.repository.DataRepository;
import com.hey.service.APIService;
import com.hey.util.GenerationUtils;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.*;

public class WsHandler {

    private UserWsChannelManager userWsChannelManager;
    private APIService apiService;
    private DataRepository dataRepository;

    public void setApiService(APIService apiService) {
        this.apiService = apiService;
    }

    public void setDataRepository(DataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    public void setUserWsChannelManager(UserWsChannelManager userWsChannelManager) {
        this.userWsChannelManager = userWsChannelManager;
    }

    public void handleChatContainerRequest(ChatContainerRequest request, String channelId, String userId) {
        String sessionId = request.getSessionId();

        Future<List<ChatItem>> futureChatItems = getChatItems(sessionId);

        futureChatItems.compose(chatItems -> {

            Future<Long> deleteUnseenCountFuture = dataRepository.deleteUnseenCount(userId, sessionId);

            deleteUnseenCountFuture.compose(deleteUnseenCountFutureRes -> {

                ChatContainerResponse response = new ChatContainerResponse();
                response.setChatGroup(false);
                response.setSessionId(sessionId);
                response.setChatItems(chatItems);
                response.setType(IWsMessage.TYPE_CHAT_ITEMS_RESPONSE);
                userWsChannelManager.selfSendMessage(response, channelId);

            }, Future.future().setHandler(handler -> {
                throw new RuntimeException(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));

    }

    public void handleChatMessageRequest(ChatMessageRequest request, String channelId, String userId) {
        if ("-1".equals(request.getSessionId())) {
            if (request.getUsernames().size() == 1) {
                insertChatMessageBetweenTwoOnNewChatSessionId(request, channelId, userId);

            } else {
                insertChatMessageGroupOnNewChatSessionId(request, channelId, userId);
            }
        } else {
            UserIdSessionIdRequest userIdSessionIdRequest = new UserIdSessionIdRequest();
            userIdSessionIdRequest.setUserId(userId);
            userIdSessionIdRequest.setSessionId(request.getSessionId());
            Future<UserIdSessionIdResponse> checkUserExistInSessionFuture = apiService.checkUserExistInSession(userIdSessionIdRequest);

            checkUserExistInSessionFuture.compose(userIdSessionIdResponse -> {
                if (userIdSessionIdResponse.getExisted()) {
                    insertChatMessageOnExistedChatSessionId(request, channelId, userId);
                }
            }, Future.future().setHandler(handler -> {
                throw new RuntimeException(handler.cause());
            }));

        }
    }

    private void insertChatMessageOnNewChatSessionId(ChatMessageRequest request, String channelId, List<String> userIds) {

        Future<List<UserFull>> getUserFullsFuture = apiService.getUserFulls(userIds);

        getUserFullsFuture.compose(userFulls -> {

            List<UserHash> userHashes = new ArrayList<>();
            for (UserFull userFull : userFulls) {
                userHashes.add(new UserHash(userFull.getUserId(), userFull.getFullName()));
            }
            String sessionId = GenerationUtils.generateId();

            JsonObject content = new JsonObject();
            content.put("message", request.getMessage());

            JsonObject messageRequest = new JsonObject();
            messageRequest.put("type", "message");
            messageRequest.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(userHashes.get(0));
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessage(messageRequest.encode());
            chatMessage.setCreatedDate(new Date());
            chatMessage.setId(UUID.randomUUID().toString());

            ChatList chatList = new ChatList();
            chatList.setSessionId(sessionId);
            chatList.setUpdatedDate(new Date());
            chatList.setUserHashes(userHashes);
            chatList.setLastMessage(chatMessage.getMessage());
            chatList.setUpdatedDate(chatMessage.getCreatedDate());

            chatList.setOwner(userIds.get(0)); // First user in session will be owner of session id
            if (userIds.size() > 2) {
                chatList.setGroup(true);
                chatList.setGroupName(request.getGroupName());
            } else {
                chatList.setGroup(false);
                chatList.setGroupName("");
            }


            Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);

            Future<ChatMessage> insertChatMessageFuture = dataRepository.insertChatMessage(chatMessage);

            List<String> userFriendIds = userIds.subList(1, userIds.size());
            Future<HashMap<String, Long>> increaseUnseenCountFuture = apiService.increaseUnseenCount(userFriendIds,
                    chatList.getSessionId());

            CompositeFuture cp = CompositeFuture.all(insertChatMessageFuture, insertChatListFuture,
                    increaseUnseenCountFuture);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {
                    NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
                    newChatSessionResponse.setType(IWsMessage.TYPE_CHAT_NEW_SESSION_RESPONSE);
                    newChatSessionResponse.setSessionId(chatMessage.getSessionId());
                    newChatSessionResponse.setTransferStatement(false);
                    newChatSessionResponse.setChangeGroupName(false);
                    // userWsChannelManager.selfSendMessage(newChatSessionResponse, channelId);
                    for (UserHash userhash : chatList.getUserHashes()) {
                        userWsChannelManager.sendMessage(newChatSessionResponse, userhash.getUserId());
                    }

                } else {
                    throw new RuntimeException(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));

    }

    private void insertChatMessageBetweenTwoOnNewChatSessionId(ChatMessageRequest request, String channelId,
                                                               String userId) {

        List<String> userIds = new ArrayList<>();
        userIds.add(userId);

        // ??? Why add username instead of user id ?
        userIds.add(request.getUsernames().get(0));

        insertChatMessageOnNewChatSessionId(request, channelId, userIds);

    }

    private void insertChatMessageGroupOnNewChatSessionId(ChatMessageRequest request, String channelId, String userId) {

        Future<List<UserAuth>> getUserAuthsFuture = apiService.getUserAuths(request.getUsernames());

        getUserAuthsFuture.compose(userAuths -> {
            List<String> userIds = new ArrayList<>();
            userIds.add(userId);
            for (UserAuth userAuth : userAuths) {
                userIds.add(userAuth.getUserId());
            }

            insertChatMessageOnNewChatSessionId(request, channelId, userIds);

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));

    }

    private void insertChatMessageOnExistedChatSessionId(ChatMessageRequest request, String channelId, String userId) {

        Future<UserFull> getUserFullFuture = dataRepository.getUserFull(userId);
        getUserFullFuture.compose(userFull -> {
            JsonObject content = new JsonObject();
            content.put("message", request.getMessage());

            JsonObject messageRequest = new JsonObject();
            messageRequest.put("type", "message");
            messageRequest.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(userFull.getUserId(), userFull.getFullName()));
            chatMessage.setSessionId(request.getSessionId());
            chatMessage.setMessage(messageRequest.encode());
            chatMessage.setCreatedDate(new Date());
            chatMessage.setId(UUID.randomUUID().toString());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = apiService
                    .insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(chatMessage);

            Future<ChatList> getChatListBySessionIdFuture = apiService
                    .getChatListBySessionId(chatMessage.getSessionId());

            CompositeFuture cp = CompositeFuture.all(insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture,
                    getChatListBySessionIdFuture);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    ChatList chatList = cp.resultAt(1);

                    ChatMessageResponse response = new ChatMessageResponse();
                    response.setType(IWsMessage.TYPE_CHAT_MESSAGE_RESPONSE);
                    response.setCreatedDate(chatMessage.getCreatedDate());
                    response.setName(userFull.getFullName());
                    response.setMessage(chatMessage.getMessage());
                    response.setSessionId(chatMessage.getSessionId());
                    response.setUserId(chatMessage.getUserHash().getUserId());
                    response.setTransferStatement(false);
                    response.setChangeGroupName(false);
                    for (UserHash userhash : chatList.getUserHashes()) {
                        userWsChannelManager.sendMessage(response, userhash.getUserId());
                    }

                } else {
                    throw new RuntimeException(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));

    }

    private Future<List<ChatItem>> getChatItems(String sessionId) {
        Future<List<ChatItem>> future = Future.future();
        List<ChatItem> chatItems = new ArrayList<>();

        Future<List<ChatMessage>> getChatMessagesFuture = apiService.getChatMessages(sessionId);

        getChatMessagesFuture.compose(chatMessages -> {

            for (ChatMessage chatMessage : chatMessages) {
                ChatItem chatItem = new ChatItem();
                chatItem.setUserId(chatMessage.getUserHash().getUserId());
                chatItem.setName(chatMessage.getUserHash().getFullName());
                chatItem.setMessage(chatMessage.getMessage());
                chatItem.setCreatedDate(chatMessage.getCreatedDate());
                chatItem.setId(chatMessage.getId());
                chatItems.add(chatItem);
            }

            chatItems.sort(Comparator.comparing(ChatItem::getCreatedDate));

            future.complete(chatItems);

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<List<FriendList>> getFriendLists(String userId) {
        return apiService.getFriendLists(userId);
    }

    public Future<UserFull> getUserFull(String userId) {
        return dataRepository.getUserFull(userId);
    }
}
