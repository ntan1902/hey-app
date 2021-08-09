package com.hey.handler.ws;

import com.hey.handler.api.BaseHandler;
import com.hey.manager.UserWsChannelManager;
import com.hey.model.*;
import com.hey.repository.DataRepository;
import com.hey.service.APIService;
import com.hey.util.GenerationUtils;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
                response.setType(IWsMessage.TYPE_CHAT_ITEM_RESPONSE);
                userWsChannelManager.selfSendMessage(response, channelId);

            }, Future.future().setHandler(handler -> {
                throw new RuntimeException(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));

    }

    public void handleAddFriendRequest(ChatContainerRequest request, String channelId, String userId) {
        String sessionId = request.getSessionId();

        AddFriendRequest addFriendRequest = new AddFriendRequest();
        addFriendRequest.setUsername(sessionId);

        Future<AddFriendResponse> addWaitingFriendFuture = apiService.addWaitingFriend(addFriendRequest, userId);

        addWaitingFriendFuture.compose(addWaitingFriendResponse -> {
            AddressBookItem responseItem = addWaitingFriendResponse.getItem();

            NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
            newChatSessionResponse.setType(IWsMessage.TYPE_NOTIFICATION_ADD_FRIEND_RESPONSE);
            newChatSessionResponse.setSessionId(userId);
            userWsChannelManager.sendMessage(newChatSessionResponse, responseItem.getUserId());

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));
    }

    public void handleAddFriendToSessionRequest(AddFriendToSessionRequest request, String channelId, String userId) {
        String sessionId = request.getSessionId();

        Future<ChatList> getChatListBySessionIdFuture = apiService
                .getChatListBySessionId(sessionId);

        getChatListBySessionIdFuture.compose(chatList -> {

            Future<Long> deleteSessionFuture = dataRepository
                    .deleteSessionKey(chatList);

            deleteSessionFuture.compose(res -> {
                List<String> userIds = new ArrayList<String>();
                userIds.add(request.getUserId());
                Future<List<UserFull>> getUserFullsFuture = apiService.getUserFulls(userIds);

                getUserFullsFuture.compose(userFulls -> {

                    List<UserHash> userHashes = chatList.getUserHashes();
                    UserHash me = userHashes.get(0);
                    for (UserFull userFull : userFulls) {
                        if(userFull.getUserId() == userId) me = new UserHash(userFull.getUserId(), userFull.getFullName());
                        userHashes.add(new UserHash(userFull.getUserId(), userFull.getFullName()));
                    }

                    JsonObject content = new JsonObject();
                    content.put("message", "Add " + userFulls.get(0).getFullName() + " to group");

                    JsonObject messageRequest = new JsonObject();
                    messageRequest.put("type", "message");
                    messageRequest.put("content", content);


                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setUserHash(me);
                    chatMessage.setSessionId(sessionId);
                    chatMessage.setMessage(messageRequest.encode());
                    chatMessage.setCreatedDate(new Date());

                    chatList.setSessionId(sessionId);
                    chatList.setUpdatedDate(new Date());
                    chatList.setUserHashes(userHashes);
                    chatList.setLastMessage(chatMessage.getMessage());
                    chatList.setUpdatedDate(chatMessage.getCreatedDate());

                    Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);

                    Future<ChatMessage> insertChatMessageFuture = dataRepository.insertChatMessage(chatMessage);

                    List<String> userFriendIds = userIds.subList(1, userIds.size());
                    Future<HashMap<String, Long>> increaseUnseenCountFuture = apiService.increaseUnseenCount(userFriendIds,
                            chatList.getSessionId());

                    CompositeFuture cp = CompositeFuture.all(insertChatMessageFuture, insertChatListFuture,
                            increaseUnseenCountFuture);
                    UserHash finalMe = me;
                    cp.setHandler(ar -> {
                        if (ar.succeeded()) {
                            NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
                            newChatSessionResponse.setType(IWsMessage.TYPE_CHAT_NEW_SESSION_RESPONSE);
                            newChatSessionResponse.setSessionId(chatMessage.getSessionId());
                            // userWsChannelManager.selfSendMessage(newChatSessionResponse, channelId);
                            userWsChannelManager.sendMessage(newChatSessionResponse, request.getUserId());


                            ChatMessageResponse newMessageResponse = new ChatMessageResponse();
                            newMessageResponse.setMessage(chatMessage.getMessage());
                            newMessageResponse.setSessionId(chatMessage.getSessionId());
                            newMessageResponse.setName(finalMe.getFullName());
                            newMessageResponse.setUserId(chatMessage.getUserHash().getUserId());
                            newMessageResponse.setCreatedDate(chatMessage.getCreatedDate());
                            newMessageResponse.setType(IWsMessage.TYPE_CHAT_MESSAGE_RESPONSE);

                            for (UserHash userhash : chatList.getUserHashes()) {
                                userWsChannelManager.sendMessage(newMessageResponse, userhash.getUserId());
                            }

                        } else {
                            throw new RuntimeException(ar.cause());
                        }
                    });

                }, Future.future().setHandler(handler -> {
                    throw new RuntimeException(handler.cause());
                }));
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

            insertChatMessageOnExistedChatSessionId(request, channelId, userId);
        }
    }

    private void insertChatMessageOnNewChatSessionId(ChatMessageRequest request, String channelId,
            List<String> userIds) {

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

            ChatList chatList = new ChatList();
            chatList.setSessionId(sessionId);
            chatList.setUpdatedDate(new Date());
            chatList.setUserHashes(userHashes);
            chatList.setLastMessage(chatMessage.getMessage());
            chatList.setUpdatedDate(chatMessage.getCreatedDate());

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

                chatItems.add(chatItem);
            }

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
