package com.hey.cache.client;

import com.hey.model.*;
import com.hey.repository.DataRepository;
import com.hey.util.PropertiesUtils;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.redis.RedisClient;
import io.vertx.redis.op.ScanOptions;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class RedisCacheClient implements DataRepository {

    private static final Logger LOGGER = LogManager.getLogger(RedisCacheClient.class);

    private RedisClient client;

    private static int numScanCount;
    private String key;

    public RedisCacheClient(RedisClient client) {
        this.client = client;
        numScanCount = Integer.parseInt(PropertiesUtils.getInstance().getValue("scan.count"));
    }

    String generateUserAuthKey(String userName) {
        return "user_auth:" + userName;
    }

    String generateUserFullKey(String userId) {
        return "user_full:" + userId;
    }

    String generateFriendListKey(List<String> userIds) {

        return "friend:list:" + String.join(":", userIds);
    }

    String generateFriendListKey(String userId1, String userId2) {

        return "friend:list:" + userId1 + ":" + userId2;
    }

    String generateWaitingFriendListKey(String userId1, String userId2) {

        return "waiting_friend:list:" + userId1 + ":" + userId2;
    }

    String generateWaitingFriendListKey(List<String> userIds) {

        return "waiting_friend:list:" + String.join(":", userIds);
    }

    String generateUserStatusKey(String userId) {

        return "user_status:" + userId;
    }

    String generateChatListKey(String sessionId, List<String> userIds) {

        return "chat:list:" + sessionId + ":" + String.join(":", userIds);
    }

    String generateChatMessageKey(String sessionId, String createdDate) {

        return "chat:message:" + sessionId + ":" + createdDate;
    }

    String generateUnSeenKey(String userId, String sessionId) {

        return "unseen:" + userId + ":" + sessionId;
    }

    @Override
    public Future<List<String>> getKeysByPattern(String keyPattern) {
        Future<List<String>> future = Future.future();
        List<String> keys = new ArrayList<>();

        ScanOptions scanOptions = new ScanOptions();
        scanOptions.setCount(numScanCount);
        scanOptions.setMatch(keyPattern);
        client.scan("0", scanOptions, res -> {
            if (res.succeeded()) {

                JsonArray jsonArray = (JsonArray) res.result().getList().get(1);
                jsonArray.forEach(object -> {
                    if (object instanceof String) {
                        keys.add((String) object);
                    }
                });

                future.complete(keys);

            } else {
                future.fail(res.cause());
            }

        });

        return future;
    }

    @Override
    public Future<UserAuth> insertUserAuth(UserAuth userAuth) {

        Future<UserAuth> future = Future.future();

        JsonObject userAuthJsonObject = new JsonObject();
        userAuthJsonObject.put("user_id", userAuth.getUserId());

        client.hmset(generateUserAuthKey(userAuth.getUserName()), userAuthJsonObject, resInsertUserAuth -> {
            if (resInsertUserAuth.succeeded()) {

                future.complete(userAuth);

            } else {
                future.fail(resInsertUserAuth.cause());
            }
        });

        return future;
    }

    @Override
    public Future<UserAuth> getUserAuth(String userName) {

        Future<UserAuth> future = Future.future();

        client.hgetall(generateUserAuthKey(userName), res -> {
            if (res.succeeded()) {

                if (res.result().getString("user_id") != null) {
                    UserAuth userAuth = new UserAuth();
                    userAuth.setUserName(userName);
                    userAuth.setUserId(res.result().getString("user_id"));
                    future.complete(userAuth);
                } else {
                    future.complete(null);
                }

            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<UserFull> insertUserFull(UserFull userFull) {
        Future<UserFull> future = Future.future();

        JsonObject userFullJsonObject = new JsonObject();
        userFullJsonObject.put("user_name", userFull.getUserName());
        userFullJsonObject.put("full_name", userFull.getFullName());

        client.hmset(generateUserFullKey(userFull.getUserId()), userFullJsonObject, resInsertUserFull -> {
            if (resInsertUserFull.succeeded()) {
                future.complete(userFull);
            } else {
                future.fail(resInsertUserFull.cause());
            }

        });

        return future;
    }

    @Override
    public Future<UserFull> getUserFull(String userId) {

        Future<UserFull> future = Future.future();

        client.hgetall(generateUserFullKey(userId), res -> {
            if (res.succeeded()) {
                if (res.result().getString("user_name") != null) {
                    UserFull userFull = new UserFull();
                    userFull.setUserId(userId);
                    userFull.setUserName(res.result().getString("user_name"));
                    userFull.setFullName(res.result().getString("full_name"));

                    future.complete(userFull);
                } else {
                    future.complete(null);
                }

            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<UserStatus> insertUserStatus(UserStatus userStatus) {

        Future<UserStatus> future = Future.future();

        client.set(generateUserStatusKey(userStatus.getUserId()), userStatus.getStatus(), res -> {
            if (res.succeeded()) {
                future.complete(userStatus);
            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<UserStatus> getUserStatus(String userId) {

        Future<UserStatus> future = Future.future();

        client.get(generateUserStatusKey(userId), res -> {

            if (res.succeeded()) {
                if (res.result() != null) {
                    UserStatus userStatus = new UserStatus();
                    userStatus.setUserId(userId);
                    userStatus.setStatus(res.result());

                    future.complete(userStatus);
                } else {
                    future.complete(null);
                }
            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<FriendList> insertFriendList(FriendList friendList) {

        Future<FriendList> future = Future.future();

        JsonObject friendListJsonObject = new JsonObject();
        List<String> userIds = new ArrayList<>();
        userIds.add(friendList.getCurrentUserHashes().getUserId());
        userIds.add(friendList.getFriendUserHashes().getUserId());
        friendListJsonObject.put(friendList.getCurrentUserHashes().getUserId(), friendList.getCurrentUserHashes().getFullName());
        friendListJsonObject.put(friendList.getFriendUserHashes().getUserId(), friendList.getFriendUserHashes().getFullName());

        client.hmset(generateFriendListKey(userIds), friendListJsonObject, res -> {
            if (res.succeeded()) {
                future.complete(friendList);
            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<FriendList> insertWaitingFriendList(FriendList friendList) {

        Future<FriendList> future = Future.future();

        JsonObject friendListJsonObject = new JsonObject();
        List<String> userIds = new ArrayList<>();
        userIds.add(friendList.getCurrentUserHashes().getUserId());
        userIds.add(friendList.getFriendUserHashes().getUserId());
        friendListJsonObject.put(friendList.getCurrentUserHashes().getUserId(), friendList.getCurrentUserHashes().getFullName());
        friendListJsonObject.put(friendList.getFriendUserHashes().getUserId(), friendList.getFriendUserHashes().getFullName());

        client.hmset(generateWaitingFriendListKey(userIds), friendListJsonObject, res -> {
            if (res.succeeded()) {
                future.complete(friendList);
            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<FriendList> getFriendList(String friendListKey, String currentUserId) {

        Future<FriendList> future = Future.future();

        client.hgetall(friendListKey, res -> {
            if (res.succeeded()) {
                Set<String> fieldNames = res.result().fieldNames();

                if (fieldNames.size() == 2) {
                    FriendList friendList = new FriendList();

                    for (String fieldName : fieldNames) {

                        if (currentUserId.equals(fieldName)) {
                            friendList.setCurrentUserHashes(new UserHash(fieldName, res.result().getString(fieldName)));
                        } else {
                            friendList.setFriendUserHashes(new UserHash(fieldName, res.result().getString(fieldName)));
                        }
                    }

                    future.complete(friendList);

                } else {
                    future.complete(null);
                }

            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<FriendList> getWaitingFriendList(String friendListKey, String currentUserId) {

        Future<FriendList> future = Future.future();

        client.hgetall(friendListKey, res -> {
            if (res.succeeded()) {
                Set<String> fieldNames = res.result().fieldNames();

                if (fieldNames.size() == 2) {
                    FriendList friendList = new FriendList();

                    for (String fieldName : fieldNames) {

                        if (currentUserId.equals(fieldName)) {
                            friendList.setCurrentUserHashes(new UserHash(fieldName, res.result().getString(fieldName)));
                        } else {
                            friendList.setFriendUserHashes(new UserHash(fieldName, res.result().getString(fieldName)));
                        }
                    }

                    future.complete(friendList);

                } else {
                    future.complete(null);
                }

            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<ChatList> insertChatList(ChatList chatList) {

        Future<ChatList> future = Future.future();

        JsonObject chatListJsonObject = new JsonObject();
        String updatedDate = String.valueOf(chatList.getUpdatedDate() != null ? chatList.getUpdatedDate().getTime() : new Date().getTime());
        chatListJsonObject.put("updated_date", updatedDate);

        List<String> userIds = new ArrayList<>();
        for (UserHash userHash : chatList.getUserHashes()) {
            chatListJsonObject.put(userHash.getUserId(), userHash.getFullName());
            userIds.add(userHash.getUserId());
        }

        chatListJsonObject.put("last_message", StringUtils.isEmpty(chatList.getLastMessage()) ? "no message" : chatList.getLastMessage());
        
        client.hmset(generateChatListKey(chatList.getSessionId(), userIds), chatListJsonObject, res -> {
            if (res.succeeded()) {
                future.complete(chatList);
            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<ChatList> getChatList(String chatListKey) {

        Future<ChatList> future = Future.future();

        client.hgetall(chatListKey, res -> {
            if (res.succeeded()) {
                ChatList chatList = convertJsonObjectToChatList(res.result(), chatListKey);

                if (chatList.getUserHashes().size() >= 2) {
                    future.complete(chatList);
                } else {
                    future.complete(null);
                }

            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<ChatMessage> insertChatMessage(ChatMessage chatMessage) {

        Future<ChatMessage> future = Future.future();

        // Insert new message
        JsonObject chatMessageJsonObject = new JsonObject();
        chatMessageJsonObject.put(chatMessage.getUserHash().getUserId(), chatMessage.getUserHash().getFullName());
        chatMessageJsonObject.put("message", chatMessage.getMessage());
        chatMessageJsonObject.put("created_date", String.valueOf(chatMessage.getCreatedDate().getTime()));

        client.hmset(generateChatMessageKey(chatMessage.getSessionId(), String.valueOf(chatMessage.getCreatedDate().getTime())), chatMessageJsonObject, resInsertChatMessage -> {
            if (resInsertChatMessage.succeeded()) {

                future.complete(chatMessage);
            } else {
                future.fail(resInsertChatMessage.cause());
            }
        });

        return future;
    }

    @Override
    public Future<ChatMessage> getChatMessage(String chatMessageKey) {
        Future<ChatMessage> future = Future.future();

        client.hgetall(chatMessageKey, res -> {
            if (res.succeeded()) {

                if (res.result().getString("message") != null) {
                    ChatMessage chatMessage = new ChatMessage();
                    Set<String> fieldNames = res.result().fieldNames();
                    for (String fieldName : fieldNames) {

                        switch (fieldName) {
                            case "message":
                                chatMessage.setMessage(res.result().getString(fieldName));
                                break;
                            case "created_date":
                                chatMessage.setCreatedDate(new Date(Long.parseLong(res.result().getString(fieldName))));
                                break;
                            default:
                                chatMessage.setUserHash(new UserHash(fieldName, res.result().getString(fieldName)));
                                break;
                        }
                    }

                    String[] componentKey = chatMessageKey.split(":");
                    if (componentKey.length > 3) {
                        chatMessage.setSessionId(componentKey[2]);
                    }

                    future.complete(chatMessage);
                } else {
                    future.complete(null);
                }
            } else {
                future.fail(res.cause());
            }
        });

        return future;
    }

    @Override
    public Future<Long> increaseUnseenCount(String userId, String sessionId) {

        Future<Long> future = Future.future();

        client.exists(generateUnSeenKey(userId, sessionId), checkExistsKey -> {
            if (checkExistsKey.succeeded()) {

                client.incr(generateUnSeenKey(userId, sessionId), increaseKey -> {
                    if (increaseKey.succeeded()) {

                        future.complete(increaseKey.result());
                    } else {
                        future.fail(increaseKey.cause());
                    }
                });

            } else {
                future.fail(checkExistsKey.cause());
            }
        });

        return future;
    }

    @Override
    public Future<Long> getUnseenCount(String userId, String sessionId) {

        Future<Long> future = Future.future();

        client.get(generateUnSeenKey(userId, sessionId), getUnseenCountRes -> {
            if (getUnseenCountRes.succeeded()) {

                if (getUnseenCountRes.result() != null) {
                    future.complete(Long.parseLong(getUnseenCountRes.result()));
                } else {
                    future.complete(Long.parseLong("0"));
                }

            } else {
                future.fail(getUnseenCountRes.cause());
            }
        });

        return future;
    }

    @Override
    public Future<Long> deleteUnseenCount(String userId, String sessionId) {
        Future<Long> future = Future.future();

        client.del(generateUnSeenKey(userId, sessionId), deleteUnseenCountRes -> {
            if (deleteUnseenCountRes.succeeded()) {
                future.complete(deleteUnseenCountRes.result());

            } else {
                future.fail(deleteUnseenCountRes.cause());
            }
        });

        return future;
    }

    @Override
    public Future<Long> deleteFriend(String userId, String friendId) {
        Future<Long> future = Future.future();
        client.del(generateFriendListKey(userId, friendId), deleteFriendRes -> {
            if (deleteFriendRes.succeeded()) {
                future.complete(deleteFriendRes.result());

            } else {
                future.fail(deleteFriendRes.cause());
            }
        });


        return future;
    }

    @Override
    public Future<Long> deleteSessionKey(ChatList chatList) {
        Future<Long> future = Future.future();

        List<String> userIds = new ArrayList<>();
        for (UserHash userHash : chatList.getUserHashes()) {
            userIds.add(userHash.getUserId());
        }

        client.del(generateChatListKey(chatList.getSessionId(), userIds), deleteFriendRes -> {
            if (deleteFriendRes.succeeded()) {
                future.complete(deleteFriendRes.result());

            } else {
                future.fail(deleteFriendRes.cause());
            }
        });

        return future;
    }

    @Override
    public Future<Long> deleteWaitingFriend(String userId, String friendId) {
        Future<Long> future = Future.future();
        client.del(generateWaitingFriendListKey(friendId, userId), deleteFriendRes -> {
            if (deleteFriendRes.succeeded()) {
                future.complete(deleteFriendRes.result());

            } else {
                future.fail(deleteFriendRes.cause());
            }
        });

        return future;
    }

    private ChatList convertJsonObjectToChatList(JsonObject jsonObject, String chatListKey) {
        ChatList chatList = new ChatList();
        List<UserHash> userHashes = new ArrayList<>();

        // chat:list:sessionId:user1:user2
        String[] componentKey = chatListKey.split(":");
        if (componentKey.length > 3) {
            chatList.setSessionId(componentKey[2]);
        }

        chatList.setUpdatedDate(new Date(Long.parseLong(jsonObject.getString("updated_date"))));
        chatList.setLastMessage(jsonObject.getString("last_message"));
        for(int i = 3; i < componentKey.length; i++) {
            UserHash userHash = new UserHash(componentKey[i], jsonObject.getString(componentKey[i]));
            userHashes.add(userHash);
        }
        chatList.setUserHashes(userHashes);

        return chatList;
    }

    public RedisClient getClient() {
        return client;
    }

}
