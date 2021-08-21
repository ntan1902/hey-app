package com.hey.service;

import com.hey.manager.UserWsChannelManager;
import com.hey.model.*;
import com.hey.model.lucky.LuckyMoneyMessageRequest;
import com.hey.model.lucky.ReceiveLuckyMoneyMessageRequest;
import com.hey.model.lucky.UserIdSessionIdRequest;
import com.hey.model.lucky.UserIdSessionIdResponse;
import com.hey.model.payment.TransferMessageRequest;
import com.hey.util.ErrorCode;
import com.hey.util.GenerationUtils;
import com.hey.util.HeyHttpStatusException;
import com.hey.util.HttpStatus;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class APIService extends BaseService {

    private final UserWsChannelManager userWsChannelManager;
    private final AuthService authService;

    public APIService(UserWsChannelManager userWsChannelManager, AuthService authService) {
        this.userWsChannelManager = userWsChannelManager;
        this.authService = authService;
    }


    public Future<User> registerUser(String jsonData) {
        Future<User> future = Future.future();

        final User user = Json.decodeValue(jsonData, User.class);

        if (StringUtils.isBlank(user.getUserName())) {
            future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                    ErrorCode.REGISTER_USERNAME_EMPTY.code(), "User Name cannot be empty"));
            return future;
        }

        if (StringUtils.isBlank(user.getFullName())) {
            future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                    ErrorCode.REGISTER_FULLNAME_EMPTY.code(), "Full Name cannot be empty"));
            return future;
        }

        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(user.getUserName());

        getUserAuthFuture.compose(existedUserAuth -> {
            if (existedUserAuth != null) {
                throw new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        ErrorCode.REGISTER_USERNAME_UNIQUED.code(), "User Name is duplicated");
            } else {

                Future<User> insertUserFuture = insertUser(user);

                insertUserFuture.compose(userRes -> {

                    future.complete(userRes);

                }, Future.future().setHandler(handler -> {
                    future.fail(handler.cause());
                }));
            }

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<ChatListResponse> getChatList(String userId) {

        Future<ChatListResponse> future = Future.future();

        Future<List<ChatList>> getChatListsFuture = getChatLists(userId);

        getChatListsFuture.compose(chatLists -> {

            List<Future> getUnSeenCountFutures = new ArrayList<>();
            for (ChatList chatList : chatLists) {
                getUnSeenCountFutures.add(dataRepository.getUnseenCount(userId, chatList.getSessionId()));
            }

            CompositeFuture cp = CompositeFuture.all(getUnSeenCountFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    List<ChatListItem> chatListItems = new ArrayList<>();

                    for (int index = 0; index < getUnSeenCountFutures.size(); ++index) {
                        Long unSeenCount = cp.resultAt(index);
                        ChatList chatList = chatLists.get(index);

                        ChatListItem chatListItem = getChatListItem(userId, chatList);

                        chatListItem.setUnread(unSeenCount.intValue());
                        chatListItems.add(chatListItem);
                    }

                    ChatListResponse chatListResponse = new ChatListResponse();
                    chatListResponse.setItems(chatListItems);
                    future.complete(chatListResponse);

                } else {
                    future.fail(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    private ChatListItem getChatListItem(String userId, ChatList chatList) {
        ChatListItem chatListItem = new ChatListItem();

        List<UserHash> userHashes = chatList.getUserHashes();
        List<String> listFullNameExcludedCurrentUser = getListFullNameExcludedCurrentUser(
                userId,
                userHashes
        );

        if (listFullNameExcludedCurrentUser.size() > 1) {
            // Chat Group
            String name = listFullNameExcludedCurrentUser.stream()
                    .map(fullName -> fullName.split(" ")[0]).collect(Collectors.joining(", "));
            chatListItem.setName(name);
        } else if (listFullNameExcludedCurrentUser.size() == 1) {
            // Chat 1-1
            chatListItem.setName(listFullNameExcludedCurrentUser.get(0));
        } else {
            // Just only current user in group
            userHashes.stream()
                    .findAny()
                    .filter(userHash -> userHash.getUserId().equals(userId))
                    .ifPresentOrElse(
                            currentUserHash -> chatListItem.setName(currentUserHash.getFullName()),
                            () -> chatListItem.setName("")
                    );
        }

        chatListItem.setGroupName(chatList.getGroupName());
        chatListItem.setGroup(chatList.isGroup());

        List<String> userIds = new ArrayList<>();
        userHashes.forEach(userHash -> userIds.add(userHash.getUserId()));
        chatListItem.setUserIds(userIds);

        chatListItem.setSessionId(chatList.getSessionId());
        chatListItem.setLastMessage(chatList.getLastMessage());
        chatListItem.setUpdatedDate(chatList.getUpdatedDate());
        return chatListItem;
    }

    public Future<AddressBookResponse> getAddressBook(String userId) {

        Future<AddressBookResponse> future = Future.future();

        Future<List<FriendList>> getFriendListsFuture = getFriendLists(userId);

        getFriendListsFuture.compose(friendLists -> {

            List<AddressBookItem> addressBookItems = new ArrayList<>();

            List<Future> getUserStatusFutures = new ArrayList<>();
            List<Future> getUserOnlineFutures = new ArrayList<>();
            List<Future> getUserStatusAndUserOnlineFutures = new ArrayList<>();

            for (FriendList friendList : friendLists) {
                getUserStatusFutures.add(dataRepository.getUserStatus(friendList.getFriendUserHashes().getUserId()));
                getUserOnlineFutures.add(isUserOnline(friendList.getFriendUserHashes().getUserId()));
            }
            getUserStatusAndUserOnlineFutures.addAll(getUserStatusFutures);
            getUserStatusAndUserOnlineFutures.addAll(getUserOnlineFutures);

            CompositeFuture cp = CompositeFuture.all(getUserStatusAndUserOnlineFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    for (int index = 0; index < friendLists.size(); ++index) {

                        AddressBookItem addressBookItem = new AddressBookItem();

                        UserHash friendUserHash = friendLists.get(index).getFriendUserHashes();
                        addressBookItem.setUserId(friendUserHash.getUserId());
                        addressBookItem.setName(friendUserHash.getFullName());

                        UserStatus friendUserStatus = cp.resultAt(index);
                        Boolean isUserOnline = cp.resultAt(index + friendLists.size());

                        addressBookItem.setStatus(friendUserStatus.getStatus());
                        addressBookItem.setOnline(isUserOnline);

                        addressBookItems.add(addressBookItem);
                    }

                    AddressBookResponse addressBookResponse = new AddressBookResponse();
                    addressBookResponse.setItems(addressBookItems);
                    future.complete(addressBookResponse);

                } else {
                    future.fail(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> future.fail(handler.cause())));

        return future;
    }

    public Future<AddressBookResponse> getWaitingFriends(String userId) {

        Future<AddressBookResponse> future = Future.future();

        Future<List<FriendList>> getFriendListsFuture = getWaitingFriendLists(userId);

        getFriendListsFuture.compose(friendLists -> {

            List<AddressBookItem> addressBookItems = new ArrayList<>();

            List<Future> getUserStatusFutures = new ArrayList<>();
            List<Future> getUserOnlineFutures = new ArrayList<>();
            List<Future> getUserStatusAndUserOnlineFutures = new ArrayList<>();

            for (FriendList friendList : friendLists) {
                getUserStatusFutures.add(dataRepository.getUserStatus(friendList.getFriendUserHashes().getUserId()));
                getUserOnlineFutures.add(isUserOnline(friendList.getFriendUserHashes().getUserId()));
            }
            getUserStatusAndUserOnlineFutures.addAll(getUserStatusFutures);
            getUserStatusAndUserOnlineFutures.addAll(getUserOnlineFutures);

            CompositeFuture cp = CompositeFuture.all(getUserStatusAndUserOnlineFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    for (int index = 0; index < friendLists.size(); ++index) {

                        AddressBookItem addressBookItem = new AddressBookItem();

                        UserHash friendUserHash = friendLists.get(index).getFriendUserHashes();
                        addressBookItem.setUserId(friendUserHash.getUserId());
                        addressBookItem.setName(friendUserHash.getFullName());

                        UserStatus friendUserStatus = cp.resultAt(index);
                        Boolean isUserOnline = cp.resultAt(index + friendLists.size());

                        addressBookItem.setStatus(friendUserStatus.getStatus());
                        addressBookItem.setOnline(isUserOnline);

                        addressBookItems.add(addressBookItem);
                    }

                    AddressBookResponse addressBookResponse = new AddressBookResponse();
                    addressBookResponse.setItems(addressBookItems);
                    future.complete(addressBookResponse);

                } else {
                    future.fail(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<UsernameExistedResponse> checkUsernameExisted(UsernameExistedRequest usernameExistedRequest,
                                                                String userId) {

        Future<UsernameExistedResponse> future = Future.future();

        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(usernameExistedRequest.getUsername());

        getUserAuthFuture.compose(userAuth -> {

            if (userAuth != null) {

                Future<Boolean> isFriendFuture = isFriend(userId, userAuth.getUserId());

                isFriendFuture.compose(isFriend -> {

                    if (isFriend) {
                        UsernameExistedResponse usernameExistedResponse = new UsernameExistedResponse();
                        usernameExistedResponse.setUsername(usernameExistedRequest.getUsername());
                        usernameExistedResponse.setExisted(true);

                        future.complete(usernameExistedResponse);
                    } else {
                        throw new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                                ErrorCode.START_GROUP_CHAT_USERNAME_NOT_FRIEND.code(), "User Name is not friend");
                    }

                }, Future.future().setHandler(handler -> {
                    future.fail(handler.cause());
                }));

            } else {
                throw new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        ErrorCode.START_GROUP_CHAT_USERNAME_NOT_EXISTED.code(), "User Name is not existed");
            }

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<GetSessionIdResponse> getSessionIdByUserId(GetSessionIdRequest getSessionIdRequest, String userId) {

        Future<GetSessionIdResponse> future = Future.future();

        String chatListKey = "chat:list:*:" + userId + ":" + getSessionIdRequest.getUserId();
        String chatListKeyReverse = "chat:list:*:" + getSessionIdRequest.getUserId() + ":" + userId;

        List<Future> getKeysByPatternFutures = new ArrayList<>();

        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKey));
        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKeyReverse));

        CompositeFuture cp = CompositeFuture.all(getKeysByPatternFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                GetSessionIdResponse getSessionIdResponse = new GetSessionIdResponse();

                List<String> keys = new ArrayList<>();
                for (int index = 0; index < getKeysByPatternFutures.size(); ++index) {
                    keys.addAll(cp.resultAt(index));
                }

                if (keys.size() > 0) {
                    getSessionIdResponse.setSessionId(keys.get(0).split(":")[2]);
                } else {
                    getSessionIdResponse.setSessionId("-1");
                }

                future.complete(getSessionIdResponse);

            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    public Future<WaitingChatHeaderResponse> waitingChatHeader(WaitingChatHeaderRequest waitingChatHeaderRequest,
                                                               String userId) {

        Future<WaitingChatHeaderResponse> future = Future.future();

        Future<List<UserAuth>> getUserAuthsFuture = getUserAuths(
                Arrays.asList(waitingChatHeaderRequest.getUsernames())
        );

        getUserAuthsFuture.compose(userAuths -> {

            List<String> userFriendIds = new ArrayList<>();
            for (UserAuth userAuth : userAuths) {
                userFriendIds.add(userAuth.getUserId());
            }

            Future<List<UserFull>> getUserFullFutures = getUserFulls(userFriendIds);

            getUserFullFutures.compose(userFulls -> {

                List<String> firstNames = new ArrayList<>();
                for (UserFull userFull : userFulls) {
                    firstNames.add(userFull.getFullName().split(" ")[0]);
                }

                WaitingChatHeaderResponse waitingChatHeaderResponse = new WaitingChatHeaderResponse();

                if (waitingChatHeaderRequest.getGroupName().equals("")) {
                    waitingChatHeaderResponse.setTitle(String.join(", ", firstNames));
                } else {
                    waitingChatHeaderResponse.setTitle(waitingChatHeaderRequest.getGroupName());
                }

                future.complete(waitingChatHeaderResponse);

            }, Future.future().setHandler(handler -> {
                future.fail(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<AddFriendResponse> addFriend(AddFriendRequest addFriendRequest, String userId) {

        Future<AddFriendResponse> future = Future.future();

        if (StringUtils.isBlank(addFriendRequest.getUsername())) {
            future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                    ErrorCode.ADD_FRIEND_USERNAME_EMPTY.code(), "User Name cannot be empty"));
        }

        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(addFriendRequest.getUsername());

        getUserAuthFuture.compose(userAuth -> {

            if (userAuth != null) {

                Future<Boolean> isFriendFuture = isFriend(userId, userAuth.getUserId());

                isFriendFuture.compose(isFriend -> {

                    if (isFriend) {
                        future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                                ErrorCode.ADD_FRIEND_USERNAME_ALREADY.code(), "User Name was added as friend"));

                    } else {

                        List<String> userIds = new ArrayList<>();
                        userIds.add(userId);
                        userIds.add(userAuth.getUserId());
                        Future<List<UserFull>> getUserFullsFuture = getUserFulls(userIds);

                        getUserFullsFuture.compose(userFulls -> {

                            UserFull currentUserFull = userFulls.get(0);
                            UserFull friendUserFull = userFulls.get(1);

                            FriendList friendList = new FriendList();
                            friendList.setCurrentUserHashes(
                                    new UserHash(currentUserFull.getUserId(), currentUserFull.getFullName()));
                            friendList.setFriendUserHashes(
                                    new UserHash(friendUserFull.getUserId(), friendUserFull.getFullName()));

                            Future<FriendList> insertFriendListFuture = dataRepository.insertFriendList(friendList);

                            insertFriendListFuture.compose(friendListRes -> {

                                List<Future> getUserStatusAndUserOnlineFuture = new ArrayList<>();
                                getUserStatusAndUserOnlineFuture
                                        .add(dataRepository.getUserStatus(friendUserFull.getUserId()));
                                getUserStatusAndUserOnlineFuture.add(isUserOnline(friendUserFull.getUserId()));

                                CompositeFuture cp = CompositeFuture.all(getUserStatusAndUserOnlineFuture);
                                cp.setHandler(ar -> {
                                    if (ar.succeeded()) {

                                        UserStatus friendUserStatus = cp.resultAt(0);
                                        Boolean isUserOnline = cp.resultAt(1);

                                        AddressBookItem addressBookItem = new AddressBookItem();
                                        addressBookItem.setUserId(friendUserFull.getUserId());
                                        addressBookItem.setName(friendUserFull.getFullName());
                                        addressBookItem.setStatus(friendUserStatus.getStatus());
                                        addressBookItem.setOnline(isUserOnline);

                                        // WebSocket sends message
                                        NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
                                        newChatSessionResponse.setType(IWsMessage.TYPE_NOTIFICATION_ACCEPT_FRIEND_RESPONSE);
                                        newChatSessionResponse.setSessionId(userId);
                                        userWsChannelManager.sendMessage(newChatSessionResponse, addressBookItem.getUserId());

                                        AddFriendResponse addFriendResponse = new AddFriendResponse();
                                        addFriendResponse.setItem(addressBookItem);

                                        future.complete(addFriendResponse);
                                    } else {
                                        future.fail(ar.cause());
                                    }
                                });

                            }, Future.future().setHandler(handler -> {
                                future.fail(handler.cause());
                            }));

                        }, Future.future().setHandler(handler -> {
                            future.fail(handler.cause());
                        }));
                    }

                }, Future.future().setHandler(handler -> {
                }));

            } else {
                throw new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        ErrorCode.ADD_FRIEND_USERNAME_NOT_EXISTED.code(), "User Name is not existed");
            }

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<AddFriendResponse> addWaitingFriend(AddFriendRequest addFriendRequest, String userId) {

        Future<AddFriendResponse> future = Future.future();

        if (StringUtils.isBlank(addFriendRequest.getUsername())) {
            future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                    ErrorCode.ADD_FRIEND_USERNAME_EMPTY.code(), "User Name cannot be empty"));
        }

        Future<UserAuth> getUserAuthFuture = dataRepository.getUserAuth(addFriendRequest.getUsername());

        getUserAuthFuture.compose(userAuth -> {

            if (userAuth != null) {

                Future<Boolean> isFriendFuture = isFriend(userId, userAuth.getUserId());

                isFriendFuture.compose(isFriend -> {

                    if (isFriend) {
                        future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                                ErrorCode.ADD_FRIEND_USERNAME_ALREADY.code(), "User Name was added as friend"));

                    } else {

                        List<String> userIds = new ArrayList<>();
                        userIds.add(userId);
                        userIds.add(userAuth.getUserId());
                        Future<List<UserFull>> getUserFullsFuture = getUserFulls(userIds);

                        getUserFullsFuture.compose(userFulls -> {

                            UserFull currentUserFull = userFulls.get(0);
                            UserFull friendUserFull = userFulls.get(1);

                            FriendList friendList = new FriendList();
                            friendList.setCurrentUserHashes(
                                    new UserHash(currentUserFull.getUserId(), currentUserFull.getFullName()));
                            friendList.setFriendUserHashes(
                                    new UserHash(friendUserFull.getUserId(), friendUserFull.getFullName()));

                            Future<FriendList> insertWaitingFriendListFuture = dataRepository.insertWaitingFriendList(friendList);

                            insertWaitingFriendListFuture.compose(friendListRes -> {

                                List<Future> getUserStatusAndUserOnlineFuture = new ArrayList<>();
                                getUserStatusAndUserOnlineFuture
                                        .add(dataRepository.getUserStatus(friendUserFull.getUserId()));
                                getUserStatusAndUserOnlineFuture.add(isUserOnline(friendUserFull.getUserId()));

                                CompositeFuture cp = CompositeFuture.all(getUserStatusAndUserOnlineFuture);
                                cp.setHandler(ar -> {
                                    if (ar.succeeded()) {

                                        UserStatus friendUserStatus = cp.resultAt(0);
                                        Boolean isUserOnline = cp.resultAt(1);

                                        AddressBookItem addressBookItem = new AddressBookItem();
                                        addressBookItem.setUserId(friendUserFull.getUserId());
                                        addressBookItem.setName(friendUserFull.getFullName());
                                        addressBookItem.setStatus(friendUserStatus.getStatus());
                                        addressBookItem.setOnline(isUserOnline);

                                        // WebSocket sends message
                                        NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
                                        newChatSessionResponse.setType(IWsMessage.TYPE_NOTIFICATION_ADD_FRIEND_RESPONSE);
                                        newChatSessionResponse.setSessionId(userId);
                                        userWsChannelManager.sendMessage(newChatSessionResponse, addressBookItem.getUserId());

                                        AddFriendResponse addFriendResponse = new AddFriendResponse();
                                        addFriendResponse.setItem(addressBookItem);

                                        future.complete(addFriendResponse);
                                    } else {
                                        future.fail(ar.cause());
                                    }
                                });

                            }, Future.future().setHandler(handler -> {
                                future.fail(handler.cause());
                            }));

                        }, Future.future().setHandler(handler -> {
                            future.fail(handler.cause());
                        }));
                    }

                }, Future.future().setHandler(handler -> {
                }));

            } else {
                throw new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        ErrorCode.ADD_FRIEND_USERNAME_NOT_EXISTED.code(), "User Name is not existed");
            }

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<JsonObject> changeStatus(ChangeStatusRequest changeStatusRequest, String userId) {

        Future<JsonObject> future = Future.future();

        UserStatus userStatus = new UserStatus();
        userStatus.setUserId(userId);
        userStatus.setStatus(changeStatusRequest.getStatus());

        Future<UserStatus> insertUserStatusFuture = dataRepository.insertUserStatus(userStatus);

        insertUserStatusFuture.compose(userStatusRes -> {
            JsonObject obj = new JsonObject();
            obj.put("message", "success");
            future.complete(obj);

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<JsonObject> deleteWaitingFriend(GetSessionIdRequest getSessionIdRequest, String userId) {

        Future<JsonObject> future = Future.future();

        Future<Long> insertUserStatusFuture = dataRepository.deleteWaitingFriend(userId, getSessionIdRequest.getUserId());

        insertUserStatusFuture.compose(userStatusRes -> {
            JsonObject obj = new JsonObject();
            obj.put("message", "success");
            future.complete(obj);

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    private List<String> getListFullNameExcludedCurrentUser(String currentUserId, List<UserHash> userHashes) {
        List<String> listFullNameExcludedCurrentUser = new ArrayList<>();
        for (UserHash userHash : userHashes) {
            if (!userHash.getUserId().equals(currentUserId)) {
                listFullNameExcludedCurrentUser.add(userHash.getFullName());
            }
        }

        return listFullNameExcludedCurrentUser;
    }

    public Future<List<UserAuth>> getUserAuths(List<String> userNames) {

        Future<List<UserAuth>> future = Future.future();

        List<Future> getUserAuthFutures = new ArrayList<>();

        for (String userName : userNames) {
            getUserAuthFutures.add(dataRepository.getUserAuth(userName));
        }

        CompositeFuture cp = CompositeFuture.all(getUserAuthFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                List<UserAuth> userAuths = new ArrayList<>();
                for (int index = 0; index < getUserAuthFutures.size(); ++index) {
                    userAuths.add(cp.resultAt(index));
                }

                future.complete(userAuths);

            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    public Future<List<UserFull>> getUserFulls(List<String> userIds) {
        Future<List<UserFull>> future = Future.future();

        List<Future> getUserFullFutures = new ArrayList<>();

        for (String userId : userIds) {
            getUserFullFutures.add(dataRepository.getUserFull(userId));
        }

        CompositeFuture cp = CompositeFuture.all(getUserFullFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                List<UserFull> userFulls = new ArrayList<>();
                for (int index = 0; index < getUserFullFutures.size(); ++index) {
                    userFulls.add(cp.resultAt(index));
                }

                future.complete(userFulls);

            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    public Future<List<FriendList>> getFriendLists(String userId) {

        Future<List<FriendList>> future = Future.future();

        if (StringUtils.isBlank(userId)) {
            future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(), "", "User Id is empty"));
            return future;
        }

        String keyPattern = "friend:list:" + userId + ":*";
        String keyPatternReverse = "friend:list:*:" + userId;

        List<Future> getKeysByPatternFutures = new ArrayList<>();

        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(keyPattern));
        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(keyPatternReverse));

        CompositeFuture cp = CompositeFuture.all(getKeysByPatternFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {
                List<String> keys = new ArrayList<>();
                for (int index = 0; index < getKeysByPatternFutures.size(); ++index) {
                    keys.addAll(cp.resultAt(index));
                }

                List<Future> getFriendListFutures = new ArrayList<>();

                for (String friendListKey : keys) {
                    getFriendListFutures.add(dataRepository.getFriendList(friendListKey, userId));
                }

                CompositeFuture cp2 = CompositeFuture.all(getFriendListFutures);
                cp2.setHandler(ar2 -> {
                    if (ar2.succeeded()) {

                        List<FriendList> friendLists = new ArrayList<>();
                        for (int index = 0; index < getFriendListFutures.size(); ++index) {
                            if (cp2.resultAt(index) != null) {
                                friendLists.add(cp2.resultAt(index));
                            }
                        }
                        future.complete(friendLists);

                    } else {
                        future.fail(ar2.cause());
                    }
                });

            } else {
                future.fail(ar.cause());
            }

        });

        return future;
    }

    public Future<List<FriendList>> getWaitingFriendLists(String userId) {

        Future<List<FriendList>> future = Future.future();

        if (StringUtils.isBlank(userId)) {
            future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(), "", "User Id is empty"));
            return future;
        }

        String keyPattern = "waiting_friend:list:" + "*:" + userId;

        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(keys -> {

            List<Future> getFriendListFutures = new ArrayList<>();

            for (String friendListKey : keys) {
                getFriendListFutures.add(dataRepository.getWaitingFriendList(friendListKey, userId));
            }

            CompositeFuture cp = CompositeFuture.all(getFriendListFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    List<FriendList> friendLists = new ArrayList<>();
                    for (int index = 0; index < getFriendListFutures.size(); ++index) {
                        if (cp.resultAt(index) != null) {
                            friendLists.add(cp.resultAt(index));
                        }
                    }
                    future.complete(friendLists);

                } else {
                    future.fail(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            //future.fail(handler.cause());
        }));

        return future;
    }

    public Future<Boolean> isFriend(String currentUserId, String friendUserId) {
        Future<Boolean> future = Future.future();

        String friendListKey = "friend:list:" + currentUserId + ":" + friendUserId;
        String friendListKeyReverse = "friend:list:" + friendUserId + ":" + currentUserId;

        List<Future> getFriendListFutures = new ArrayList<>();

        getFriendListFutures.add(dataRepository.getFriendList(friendListKey, currentUserId));
        getFriendListFutures.add(dataRepository.getFriendList(friendListKeyReverse, friendUserId));

        CompositeFuture cp = CompositeFuture.all(getFriendListFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                List<FriendList> friendLists = new ArrayList<>();
                for (int index = 0; index < getFriendListFutures.size(); ++index) {
                    if (cp.resultAt(index) != null) {
                        friendLists.add(cp.resultAt(index));
                    }
                }

                if (friendLists.size() > 0) {
                    future.complete(true);
                } else {
                    future.complete(false);
                }

            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    public Future<ChatList> getChatListBySessionId(String sessionId) {

        Future<ChatList> future = Future.future();

        String keyPattern = "chat:list:" + sessionId + "*";

        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(keys -> {

            String chatListKey = keys.get(0);
            Future<ChatList> getChatListFuture = dataRepository.getChatList(chatListKey);

            getChatListFuture.compose(chatList -> {
                future.complete(chatList);

            }, Future.future().setHandler(handler -> {
                future.fail(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<List<ChatList>> getChatLists(String userId) {

        Future<List<ChatList>> future = Future.future();

        String keyPattern = "chat:list:" + "*" + userId + "*";

        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(chatListKeys -> {

            List<Future> getChatListFutures = new ArrayList<>();

            for (String chatListKey : chatListKeys) {
                getChatListFutures.add(dataRepository.getChatList(chatListKey));
            }

            CompositeFuture cp = CompositeFuture.all(getChatListFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    List<ChatList> chatLists = new ArrayList<>();
                    for (int index = 0; index < getChatListFutures.size(); ++index) {
                        chatLists.add(cp.resultAt(index));
                    }

                    future.complete(chatLists);

                } else {
                    future.fail(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<List<ChatMessage>> getChatMessages(String sessionId) {

        Future<List<ChatMessage>> future = Future.future();

        String keyPattern = "chat:message:" + sessionId + ":*";

        Future<List<String>> getKeysByPatternFuture = dataRepository.getKeysByPattern(keyPattern);

        getKeysByPatternFuture.compose(chatMessageKeys -> {

            List<Future> getChatMessageFutures = new ArrayList<>();

            // Sort message by time
            Collections.sort(chatMessageKeys);

            for (String chatMessageKey : chatMessageKeys) {
                getChatMessageFutures.add(dataRepository.getChatMessage(chatMessageKey));
            }

            CompositeFuture cp = CompositeFuture.all(getChatMessageFutures);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    List<ChatMessage> chatMessages = new ArrayList<>();
                    for (int index = 0; index < getChatMessageFutures.size(); ++index) {
                        chatMessages.add(cp.resultAt(index));
                    }

                    future.complete(chatMessages);

                } else {
                    future.fail(ar.cause());
                }
            });

        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<UserProfileResponse> getUserProfile(String userId) {

        Future<UserProfileResponse> future = Future.future();

        List<Future> getUserFullAndUserStatusFuture = new ArrayList<>();

        getUserFullAndUserStatusFuture.add(dataRepository.getUserFull(userId));
        getUserFullAndUserStatusFuture.add(dataRepository.getUserStatus(userId));

        CompositeFuture cp = CompositeFuture.all(getUserFullAndUserStatusFuture);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                UserFull userFull = cp.resultAt(0);
                UserStatus userStatus = cp.resultAt(1);

                UserProfileResponse userProfileResponse = new UserProfileResponse();
                userProfileResponse.setStatus(userStatus.getStatus());
                userProfileResponse.setUserFullName(userFull.getFullName());
                userProfileResponse.setUserName(userFull.getUserName());

                future.complete(userProfileResponse);

            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    private Future<Boolean> isUserOnline(String userId) {
        Future<Boolean> future = Future.future();
        userWsChannelManager.getChannels(userId).setHandler(event -> {
            if (event.succeeded()) {
                if (event.result() == null || event.result().isEmpty()) {
                    future.complete(false);

                } else {
                    future.complete(true);
                }
            }
        });
        return future;
    }

    // ********************************************************** //
    public Future<Boolean> transferMessage(TransferMessageRequest transferMessageRequest) {
        Future<Boolean> future = Future.future();

        // Find session id of source id and target id
        Future<String> getSessionId = getSessionIdOfUser1AndUser2(transferMessageRequest.getSourceId(),
                transferMessageRequest.getTargetId());

        getSessionId.compose(sessionId -> {
            if ("-1".equals(sessionId)) {
                insertNewChat(transferMessageRequest);
            } else {
                insertNewChatOnExistedSessionId(transferMessageRequest, sessionId);
            }
            future.complete(true);
        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<Boolean> createLuckyMoneyMessage(LuckyMoneyMessageRequest luckyMoneyMessageRequest) {
        Future<Boolean> future = Future.future();

        insertNewChatOnExistedSessionId(luckyMoneyMessageRequest);
        future.complete(true);
        return future;
    }

    private void insertNewChatOnExistedSessionId(LuckyMoneyMessageRequest luckyMoneyMessageRequest) {
        Future<UserFull> getUserFullFuture = dataRepository
                .getUserFull(luckyMoneyMessageRequest.getUserId());
        getUserFullFuture.compose(userFull -> {
            JsonObject content = new JsonObject();
            content.put("userId", luckyMoneyMessageRequest.getUserId());
            content.put("luckyMoneyId", luckyMoneyMessageRequest.getLuckyMoneyId());
            content.put("createdAt", luckyMoneyMessageRequest.getCreatedAt());
            content.put("message", luckyMoneyMessageRequest.getMessage());
            content.put("sessionId", luckyMoneyMessageRequest.getSessionId());

            JsonObject luckyMoneyResponse = new JsonObject();
            luckyMoneyResponse.put("type", "createLuckyMoney");
            luckyMoneyResponse.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(userFull.getUserId(), userFull.getFullName()));
            chatMessage.setSessionId(luckyMoneyMessageRequest.getSessionId());
            chatMessage.setMessage(luckyMoneyResponse.encode());
            chatMessage.setCreatedDate(new Date());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(
                    chatMessage);

            Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(chatMessage.getSessionId());

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
                    response.setTransferStatement(true);

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

    private void insertNewChatOnExistedSessionId(TransferMessageRequest transferMessageRequest, String sessionId) {

        Future<UserFull> getUserFullFuture = dataRepository
                .getUserFull(transferMessageRequest.getSourceId());
        getUserFullFuture.compose(userFull -> {
            JsonObject content = new JsonObject();
            content.put("sourceId", transferMessageRequest.getSourceId());
            content.put("targetId", transferMessageRequest.getTargetId());
            content.put("amount", transferMessageRequest.getAmount());
            content.put("createdAt", transferMessageRequest.getCreatedAt());
            content.put("message", transferMessageRequest.getMessage());
            content.put("sessionId", sessionId);

            JsonObject transferMessageResponse = new JsonObject();
            transferMessageResponse.put("type", "transfer");
            transferMessageResponse.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(userFull.getUserId(), userFull.getFullName()));
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessage(transferMessageResponse.encode());
            chatMessage.setCreatedDate(new Date());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(
                    chatMessage);

            Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(chatMessage.getSessionId());

            CompositeFuture cp = CompositeFuture.all(insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture,
                    getChatListBySessionIdFuture);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {

                    ChatList chatList = cp.resultAt(1);

                    // WebSocket sends message to chat
                    ChatMessageResponse response = new ChatMessageResponse();
                    response.setType(IWsMessage.TYPE_CHAT_MESSAGE_RESPONSE);
                    response.setCreatedDate(chatMessage.getCreatedDate());
                    response.setName(userFull.getFullName());
                    response.setMessage(chatMessage.getMessage());
                    response.setSessionId(chatMessage.getSessionId());
                    response.setUserId(chatMessage.getUserHash().getUserId());
                    response.setTransferStatement(true);

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

    private void insertNewChat(TransferMessageRequest transferMessageRequest) {
        List<String> userIds = new ArrayList<>();
        userIds.add(transferMessageRequest.getSourceId());
        userIds.add(transferMessageRequest.getTargetId());

        Future<List<UserFull>> getUserFullsFuture = getUserFulls(userIds);
        getUserFullsFuture.compose(userFulls -> {

            List<UserHash> userHashes = new ArrayList<>();
            for (UserFull userFull : userFulls) {
                userHashes.add(new UserHash(userFull.getUserId(), userFull.getFullName()));
            }
            String sessionId = GenerationUtils.generateId();

            JsonObject content = new JsonObject();
            content.put("sourceId", transferMessageRequest.getSourceId());
            content.put("targetId", transferMessageRequest.getTargetId());
            content.put("amount", transferMessageRequest.getAmount());
            content.put("createdAt", transferMessageRequest.getCreatedAt());
            content.put("message", transferMessageRequest.getMessage());
            content.put("sessionId", sessionId);

            JsonObject transferMessageResponse = new JsonObject();
            transferMessageResponse.put("type", "transfer");
            transferMessageResponse.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(userHashes.get(0));
            chatMessage.setSessionId(sessionId);
            chatMessage.setMessage(transferMessageResponse.encode());
            chatMessage.setCreatedDate(new Date());

            ChatList chatList = new ChatList();
            chatList.setSessionId(sessionId);
            chatList.setUpdatedDate(new Date());
            chatList.setUserHashes(userHashes);
            chatList.setLastMessage(chatMessage.getMessage());
            chatList.setUpdatedDate(chatMessage.getCreatedDate());

            chatList.setOwner(transferMessageRequest.getSourceId());
            chatList.setGroupName("");
            chatList.setGroup(false);

            Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);

            Future<ChatMessage> insertChatMessageFuture = dataRepository.insertChatMessage(chatMessage);

            List<String> userFriendIds = userIds.subList(1, userIds.size());
            Future<HashMap<String, Long>> increaseUnseenCountFuture = increaseUnseenCount(userFriendIds,
                    chatList.getSessionId());

            CompositeFuture cp = CompositeFuture.all(insertChatMessageFuture, insertChatListFuture,
                    increaseUnseenCountFuture);
            cp.setHandler(ar -> {
                if (ar.succeeded()) {
                    NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
                    newChatSessionResponse.setType(IWsMessage.TYPE_CHAT_NEW_SESSION_RESPONSE);
                    newChatSessionResponse.setSessionId(chatMessage.getSessionId());
                    newChatSessionResponse.setTransferStatement(true);
                    newChatSessionResponse.setChangeGroupName(false);

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

    public Future<UserIdSessionIdResponse> checkUserExistInSession(UserIdSessionIdRequest request) {
        Future<UserIdSessionIdResponse> future = Future.future();

        // Find list session id of user id
        Future<List<String>> getSessionIds = getSessionIdOfUser(request.getUserId());

        getSessionIds.compose(sessionIds -> {
            UserIdSessionIdResponse response = new UserIdSessionIdResponse();
            response.setExisted(sessionIds.contains(request.getSessionId()));
            future.complete(response);
        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        return future;
    }

    public Future<String> getSessionIdOfUser1AndUser2(String userId1, String userId2) {
        Future<String> future = Future.future();

        String chatListKey = "chat:list:*:" + userId1 + ":" + userId2;
        String chatListKeyReverse = "chat:list:*:" + userId2 + ":" + userId1;

        List<Future> getKeysByPatternFutures = new ArrayList<>();

        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKey));
        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKeyReverse));

        CompositeFuture cp = CompositeFuture.all(getKeysByPatternFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                String sessionId = "";

                List<String> keys = new ArrayList<>();
                for (int index = 0; index < getKeysByPatternFutures.size(); ++index) {
                    keys.addAll(cp.resultAt(index));
                }

                if (keys.size() > 0) {
                    sessionId = keys.get(0).split(":")[2];
                } else {
                    sessionId = "-1";
                }

                future.complete(sessionId);
            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    public Future<List<String>> getSessionIdOfUser(String userId) {
        Future<List<String>> future = Future.future();

        String chatListKey = "chat:list:*:*" + userId + "*";

        List<Future> getKeysByPatternFutures = new ArrayList<>();

        getKeysByPatternFutures.add(dataRepository.getKeysByPattern(chatListKey));

        CompositeFuture cp = CompositeFuture.all(getKeysByPatternFutures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {

                List<String> sessionIds = new ArrayList<>();

                List<String> keys = new ArrayList<>();
                for (int index = 0; index < getKeysByPatternFutures.size(); ++index) {
                    keys.addAll(cp.resultAt(index));
                }

                if (keys.size() > 0) {
                    // chat:list:sessionId:user:user:...
                    sessionIds = keys.stream().map(key -> key.split(":")[2]).collect(Collectors.toList());
                }

                future.complete(sessionIds);
            } else {
                future.fail(ar.cause());
            }
        });

        return future;
    }

    public Future<Boolean> receiveLuckyMoneyMessage(ReceiveLuckyMoneyMessageRequest request) {
        Future<Boolean> future = Future.future();

        insertNewChatOnExistedSessionId(request);
        future.complete(true);
        return future;
    }

    private void insertNewChatOnExistedSessionId(ReceiveLuckyMoneyMessageRequest request) {
        Future<UserFull> getUserFullFuture = dataRepository.getUserFull(request.getReceiverId());
        getUserFullFuture.compose(userFull -> {
            JsonObject content = new JsonObject();
            content.put("userId", request.getReceiverId());
            content.put("luckyMoneyId", request.getLuckyMoneyId());
            content.put("createdAt", request.getCreatedAt());
            content.put("message", request.getMessage());
            content.put("sessionId", request.getSessionId());
            content.put("amount", request.getAmount());

            JsonObject receiveLuckyReponse = new JsonObject();
            receiveLuckyReponse.put("type", "receiveLuckyMoney");
            receiveLuckyReponse.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(userFull.getUserId(), userFull.getFullName()));
            chatMessage.setSessionId(request.getSessionId());
            chatMessage.setMessage(receiveLuckyReponse.encode());
            chatMessage.setCreatedDate(new Date());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(
                    chatMessage);

            Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(chatMessage.getSessionId());

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
                    response.setTransferStatement(true);
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

    public Future<JsonObject> editProfile(EditProfileRequest editProfileRequest, String userId) {
        Future<JsonObject> future = Future.future();
        List<Future> futures = new ArrayList<>();

        // UserFull
        Future<UserFull> getUserFullFuture = dataRepository.getUserFull(userId);
        getUserFullFuture.compose(userFull -> {
            userFull.setUserId(userId);
            userFull.setFullName(editProfileRequest.getFullName());
            futures.add(dataRepository.insertUserFull(userFull));
        }, Future.future().setHandler(handler -> {
            future.fail(handler.cause());
        }));

        // ChatList
        Future<List<ChatList>> getChatListsFuture = getChatLists(userId);
        getChatListsFuture.compose(chatLists -> {
            chatLists.forEach(chatList -> {
                // Update UserHashes in ChatList
                List<UserHash> userHashes = chatList.getUserHashes();

                userHashes.forEach(userHash -> {
                    if (userHash.getUserId().equals(userId)) {
                        userHash.setFullName(editProfileRequest.getFullName());
                    }
                });
                chatList.setUserHashes(userHashes);
                futures.add(dataRepository.insertChatList(chatList));

                // Update ChatMessage of Session Id
                Future<List<ChatMessage>> getChatMessagesFuture = getChatMessages(chatList.getSessionId());

                getChatMessagesFuture.compose(chatMessages -> {
                    chatMessages.forEach(chatMessage -> {
                        UserHash userHash = chatMessage.getUserHash();
                        if (userHash.getUserId().equals(userId)) {
                            userHash.setFullName(editProfileRequest.getFullName());
                        }
                        chatMessage.setUserHash(userHash);
                        futures.add(dataRepository.insertChatMessage(chatMessage));

                    });
                }, Future.future().setHandler(handler -> future.fail(handler.cause())));
            });
        }, Future.future().setHandler(handler -> future.fail(handler.cause())));


        // FriendList
        Future<List<FriendList>> getFriendListsFuture = getFriendLists(userId);
        getFriendListsFuture.compose(friendLists -> {
            friendLists.forEach(friendList -> {
                UserHash friendUserHashes = friendList.getFriendUserHashes();
                UserHash currentUserHashes = friendList.getCurrentUserHashes();
                if (friendUserHashes.getUserId().equals(userId)) {
                    friendUserHashes.setFullName(editProfileRequest.getFullName());
                }
                if (currentUserHashes.getUserId().equals(userId)) {
                    currentUserHashes.setFullName(editProfileRequest.getFullName());
                }

                friendList.setFriendUserHashes(friendUserHashes);
                friendList.setCurrentUserHashes(currentUserHashes);
                futures.add(dataRepository.insertFriendList(friendList));
            });
        }, Future.future().setHandler(handler -> future.fail(handler.cause())));

        CompositeFuture cp = CompositeFuture.all(futures);
        cp.setHandler(ar -> {
            if (ar.succeeded()) {
                authService.editProfile(editProfileRequest, userId, event -> {
                    if (event.succeeded()) {
                        future.complete(event.result());
                    } else {
                        future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                                "400", event.cause().getMessage()));
                    }
                });
            } else {
                future.fail(ar.cause());
            }
        });
        return future;
    }

    public Future<JsonObject> editGroupName(EditGroupNameRequest editGroupNameRequest, String userId) {
        Future<JsonObject> future = Future.future();

        String sessionId = editGroupNameRequest.getSessionId();
        String groupName = editGroupNameRequest.getGroupName();

        // ChatList
        Future<ChatList> getChatListFuture = getChatListBySessionId(sessionId);
        getChatListFuture.compose(chatList -> {
            // Update UserHashes in ChatList
            List<UserHash> userHashes = chatList.getUserHashes();

            boolean isUserIdInSessionId = userHashes.stream()
                    .anyMatch(userHash -> userHash.getUserId().equals(userId));

            if (isUserIdInSessionId
                    && chatList.isGroup()) {
                chatList.setGroupName(groupName);

                Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);
                insertChatListFuture.compose(res -> {
                    insertNewChatOnExistedSessionId(editGroupNameRequest, userId, userHashes);

                    JsonObject apiResponse = new JsonObject();
                    apiResponse.put("success", true);
                    apiResponse.put("message", "Edit group name successfully");
                    apiResponse.put("code", 201);
                    apiResponse.put("payload", "");
                    future.complete(apiResponse);

                }, Future.future().setHandler(handler -> future.fail(handler.cause())));
            } else {
                future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        "400", "Edit group name unsuccessfully"));
            }

        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }

    private void insertNewChatOnExistedSessionId(EditGroupNameRequest editGroupNameRequest, String userId, List<UserHash> userHashes) {
        Future<UserFull> getUserFullFuture = dataRepository.getUserFull(userId);
        getUserFullFuture.compose(userFull -> {
            JsonObject content = new JsonObject();
            content.put("message", userFull.getFullName() + " edit group name into: " + editGroupNameRequest.getGroupName());
            content.put("groupName", editGroupNameRequest.getGroupName());

            JsonObject editGroupNameResponse = new JsonObject();
            editGroupNameResponse.put("type", "message");
            editGroupNameResponse.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(userFull.getUserId(), userFull.getFullName()));
            chatMessage.setSessionId(editGroupNameRequest.getSessionId());
            chatMessage.setMessage(editGroupNameResponse.encode());
            chatMessage.setCreatedDate(new Date());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(
                    chatMessage);

            insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture.compose(resChatMessage -> {
                ChatMessageResponse response = new ChatMessageResponse();
                response.setType(IWsMessage.TYPE_CHAT_MESSAGE_RESPONSE);
                response.setCreatedDate(chatMessage.getCreatedDate());
                response.setName(userFull.getFullName());
                response.setMessage(chatMessage.getMessage());
                response.setSessionId(chatMessage.getSessionId());
                response.setUserId(chatMessage.getUserHash().getUserId());
                response.setTransferStatement(false);
                response.setChangeGroupName(true);
                for (UserHash userhash : userHashes) {
                    userWsChannelManager.sendMessage(response, userhash.getUserId());
                }

            }, Future.future().setHandler(handler -> {
                throw new RuntimeException(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));
    }

    public Future<JsonObject> kickMember(KickMemberRequest kickMemberRequest, String userId) {
        Future<JsonObject> future = Future.future();

        String sessionId = kickMemberRequest.getSessionId();
        String memberId = kickMemberRequest.getMemberId();

        // ChatList
        Future<ChatList> getChatListFuture = getChatListBySessionId(sessionId);
        getChatListFuture.compose(chatList -> {
            // Update UserHashes in ChatList
            List<UserHash> userHashes = chatList.getUserHashes();

            boolean isUserIdInSessionId = false;
            boolean isMemberIdInSessionId = false;
            for (UserHash userHash : userHashes) {
                if (userHash.getUserId().equals(userId)) {
                    isUserIdInSessionId = true;
                }
                if (userHash.getUserId().equals(memberId)) {
                    isMemberIdInSessionId = true;
                }
            }

            if (!userId.equals(memberId)
                    && chatList.isGroup()
                    && isUserIdInSessionId
                    && isMemberIdInSessionId
                    && chatList.getOwner().equals(userId)) {

                Future<Long> deleteSessionFuture = dataRepository.deleteSessionKey(chatList);
                deleteSessionFuture.compose(res -> {

                    List<UserHash> kickedUserHashes = userHashes.stream()
                            .filter(userHash -> !userHash.getUserId().equals(memberId))
                            .collect(Collectors.toList());

                    chatList.setUserHashes(kickedUserHashes);

                    Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);
                    insertChatListFuture.compose(res2 -> {

                        insertNewChatOnExistedSessionId(kickMemberRequest, userId, userHashes);

                        JsonObject apiResponse = new JsonObject();
                        apiResponse.put("success", true);
                        apiResponse.put("message", "Kick member successfully");
                        apiResponse.put("code", 201);
                        apiResponse.put("payload", "");


                        future.complete(apiResponse);

                    }, Future.future().setHandler(handler -> future.fail(handler.cause())));

                }, Future.future().setHandler(handler -> future.fail(handler.cause())));


            } else {
                future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        "400", "Kick member unsuccessfully"));
            }

        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }

    private void insertNewChatOnExistedSessionId(KickMemberRequest kickMemberRequest, String userId, List<UserHash> userHashes) {
        Future<UserFull> getUserFullFuture = dataRepository
                .getUserFull(userId);

        Future<UserFull> getMemberFuture = dataRepository.getUserFull(kickMemberRequest.getMemberId());

        CompositeFuture cp = CompositeFuture.all(getUserFullFuture, getMemberFuture);
        cp.compose(res -> {
            UserFull user = res.resultAt(0);
            UserFull member = res.resultAt(1);

            JsonObject content = new JsonObject();
            content.put("message", user.getFullName() + " kick " + member.getFullName());

            JsonObject messageKickMember = new JsonObject();
            messageKickMember.put("type", "message");
            messageKickMember.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(user.getUserId(), user.getFullName()));
            chatMessage.setSessionId(kickMemberRequest.getSessionId());
            chatMessage.setMessage(messageKickMember.encode());
            chatMessage.setCreatedDate(new Date());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(chatMessage);

            insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture.compose(resChatMessge -> {
                ChatMessageResponse response = new ChatMessageResponse();
                response.setType(IWsMessage.TYPE_CHAT_MESSAGE_RESPONSE);
                response.setCreatedDate(chatMessage.getCreatedDate());
                response.setName(user.getFullName());
                response.setMessage(chatMessage.getMessage());
                response.setSessionId(chatMessage.getSessionId());
                response.setUserId(chatMessage.getUserHash().getUserId());
                response.setTransferStatement(false);
                response.setChangeGroupName(false);
                for (UserHash userhash : userHashes) {
                    userWsChannelManager.sendMessage(response, userhash.getUserId());
                }

            }, Future.future().setHandler(handler -> {
                throw new RuntimeException(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));
    }

    public Future<JsonObject> outGroup(OutGroupRequest outGroupRequest, String userId) {
        Future<JsonObject> future = Future.future();

        String sessionId = outGroupRequest.getSessionId();

        // ChatList
        Future<ChatList> getChatListFuture = getChatListBySessionId(sessionId);
        getChatListFuture.compose(chatList -> {
            // Update UserHashes in ChatList
            List<UserHash> userHashes = chatList.getUserHashes();

            boolean isUserIdInSessionId = userHashes.stream()
                    .anyMatch(userHash -> userHash.getUserId().equals(userId));
            if (chatList.isGroup()
                    && isUserIdInSessionId) {

                Future<Long> deleteSessionFuture = dataRepository.deleteSessionKey(chatList);
                deleteSessionFuture.compose(res -> {
                    List<UserHash> outGroupUserHashes = userHashes.stream()
                            .filter(userHash -> !userHash.getUserId().equals(userId))
                            .collect(Collectors.toList());

                    chatList.setUserHashes(outGroupUserHashes);
                    if (chatList.getOwner().equals(userId)
                            && !outGroupUserHashes.isEmpty()) {
                        chatList.setOwner(outGroupUserHashes.get(0).getUserId());
                    }

                    Future<ChatList> insertChatListFuture = dataRepository.insertChatList(chatList);
                    insertChatListFuture.compose(res2 -> {

                        insertNewChatOnExistedSessionId(outGroupRequest, userId, userHashes);

                        JsonObject apiResponse = new JsonObject();
                        apiResponse.put("success", true);
                        apiResponse.put("message", "Out group successfully");
                        apiResponse.put("code", 201);
                        apiResponse.put("payload", "");
                        future.complete(apiResponse);

                    }, Future.future().setHandler(handler -> future.fail(handler.cause())));

                }, Future.future().setHandler(handler -> future.fail(handler.cause())));


            } else {
                future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(),
                        "400", "Out group unsuccessfully"));
            }

        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }

    private void insertNewChatOnExistedSessionId(OutGroupRequest outGroupRequest, String userId, List<UserHash> userHashes) {
        Future<UserFull> getUserFullFuture = dataRepository.getUserFull(userId);
        getUserFullFuture.compose(userFull -> {
            JsonObject content = new JsonObject();
            content.put("message", userFull.getFullName() + " leave group");

            JsonObject receiveLuckyReponse = new JsonObject();
            receiveLuckyReponse.put("type", "message");
            receiveLuckyReponse.put("content", content);

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setUserHash(new UserHash(userFull.getUserId(), userFull.getFullName()));
            chatMessage.setSessionId(outGroupRequest.getSessionId());
            chatMessage.setMessage(receiveLuckyReponse.encode());
            chatMessage.setCreatedDate(new Date());

            Future<ChatMessage> insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture = insertChatMessagesAndUpdateChatListAndUpdateUnseenCount(
                    chatMessage);

            insertChatMessagesAndUpdateChatListAndUpdateUnseenCountFuture.compose(resChatMessge -> {
                ChatMessageResponse response = new ChatMessageResponse();
                response.setType(IWsMessage.TYPE_CHAT_MESSAGE_RESPONSE);
                response.setCreatedDate(chatMessage.getCreatedDate());
                response.setName(userFull.getFullName());
                response.setMessage(chatMessage.getMessage());
                response.setSessionId(chatMessage.getSessionId());
                response.setUserId(chatMessage.getUserHash().getUserId());
                response.setTransferStatement(false);
                response.setChangeGroupName(false);
                for (UserHash userhash : userHashes) {
                    userWsChannelManager.sendMessage(response, userhash.getUserId());
                }

            }, Future.future().setHandler(handler -> {
                throw new RuntimeException(handler.cause());
            }));

        }, Future.future().setHandler(handler -> {
            throw new RuntimeException(handler.cause());
        }));
    }


    public Future<JsonObject> getMembersOfSessionChat(String userId, String sessionId) {
        Future<JsonObject> future = Future.future();

        UserIdSessionIdRequest userIdSessionIdRequest = new UserIdSessionIdRequest();
        userIdSessionIdRequest.setSessionId(sessionId);
        userIdSessionIdRequest.setUserId(userId);

        Future<UserIdSessionIdResponse> checkUserInSessionFuture = checkUserExistInSession(userIdSessionIdRequest);

        checkUserInSessionFuture.compose(userIdSessionIdResponse -> {
            if (userIdSessionIdResponse.getExisted()) {
                Future<ChatList> getChatListFuture = getChatListBySessionId(sessionId);
                getChatListFuture.compose(chatList -> {
                    JsonObject payload = new JsonObject();
                    payload.put("isOwner", userId.equals(chatList.getOwner()));
                    payload.put("members", chatList.getUserHashes());
                    JsonObject apiResponse = new JsonObject();
                    apiResponse.put("success", true);
                    apiResponse.put("message", "");
                    apiResponse.put("code", 200);
                    apiResponse.put("payload", payload);
                    future.complete(apiResponse);
                }, Future.future().setHandler(handler -> future.fail(handler.cause())));
            } else {
                future.fail(new HeyHttpStatusException(HttpStatus.BAD_REQUEST.code(), "400", "You aren't in that group!"));
            }
        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }

    public Future<JsonObject> makeCall(MakeCallRequest makeCallRequest, String userId) {
        Future<JsonObject> future = Future.future();
        Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(makeCallRequest.getSessionId());
        getChatListBySessionIdFuture.compose(chatList -> {
            HaveCallDTO haveCallDTO = new HaveCallDTO();
            haveCallDTO.setType(IWsMessage.HAVE_CALL);
            haveCallDTO.setGroupName(chatList.getGroupName().equals("") ? chatList.getSessionId() : chatList.getGroupName());
            haveCallDTO.setSessionId(chatList.getSessionId());
            haveCallDTO.setVideoCall(makeCallRequest.getIsVideoCall());
            for (UserHash userhash : chatList.getUserHashes()) {
                if (!userhash.getUserId().equals(userId)) {
                    userWsChannelManager.sendMessage(haveCallDTO, userhash.getUserId());
                }
            }

            JsonObject apiResponse = new JsonObject();
            apiResponse.put("success", true);
            apiResponse.put("code", 200);
            apiResponse.put("message", "Make call successfully");
            apiResponse.put("payload", "");
            future.complete(apiResponse);

        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }

    public Future<JsonObject> joinCall(JoinCallRequest joinCallRequest, String userId) {
        Future<JsonObject> future = Future.future();
        Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(joinCallRequest.getSessionId());
        getChatListBySessionIdFuture.compose(chatList -> {
            JoinCallDTO joinCallDTO = new JoinCallDTO();
            joinCallDTO.setType(IWsMessage.JOIN_CALL);
            joinCallDTO.setPeerId(joinCallRequest.getPeerId());
            for (UserHash userhash : chatList.getUserHashes()) {
                if (!userhash.getUserId().equals(userId)) {
                    userWsChannelManager.sendMessage(joinCallDTO, userhash.getUserId());
                }
            }

            JsonObject apiResponse = new JsonObject();
            apiResponse.put("success", true);
            apiResponse.put("code", 200);
            apiResponse.put("message", "Join call successfully");
            apiResponse.put("payload", "");
            future.complete(apiResponse);

        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }

    public Future<JsonObject> getICEServer() {
        Future<JsonObject> future = Future.future();
        JsonObject request = new JsonObject();
        request.put("format", "urls");
        webClient.putAbs("https://global.xirsys.net/_turn/hey-app")
                .putHeader("Authorization", "Basic bmdvY3Ryb25nMTAyOjBjZjRhZDMyLTk1ZTItMTFlYi05YzJkLTAyNDJhYzE1MDAwMg==")
                .putHeader("Content-Type", "application/json")
                .putHeader("Content-Length", "17")
                .sendJsonObject(request, httpResponseAsyncResult -> {
                    if (httpResponseAsyncResult.succeeded()) {
                        JsonObject iceServers = httpResponseAsyncResult.result().bodyAsJsonObject().getJsonObject("v").getJsonObject("iceServers");

                        List<JsonObject> servers = iceServers.getJsonArray("urls").stream().map(url -> {
                            JsonObject server = new JsonObject();
                            server.put("username", iceServers.getString("username"));
                            server.put("url", url);
                            server.put("credential", iceServers.getString("credential"));
                            return server;
                        }).collect(Collectors.toList());

                        JsonObject apiResponse = new JsonObject();
                        apiResponse.put("success", true);
                        apiResponse.put("code", 200);
                        apiResponse.put("message", "");
                        apiResponse.put("payload", servers);
                        future.complete(apiResponse);
                    }
                });


        return future;
    }

    public Future<Boolean> addFriendToSessionRequest(AddFriendToSessionRequest request, String userId) {
        Future<Boolean> future = Future.future();

        String sessionId = request.getSessionId();

        Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(sessionId);

        getChatListBySessionIdFuture.compose(chatList -> {

            Future<Long> deleteSessionFuture = dataRepository
                    .deleteSessionKey(chatList);

            deleteSessionFuture.compose(res -> {
                List<String> userIds = new ArrayList<>();
                userIds.add(request.getUserId());

                Future<List<UserFull>> getUserFullsFuture = getUserFulls(userIds);

                getUserFullsFuture.compose(userFulls -> {

                    List<UserHash> userHashes = chatList.getUserHashes();

                    UserHash me = userHashes.get(0);
                    for (UserFull userFull : userFulls) {
                        if (userFull.getUserId().equals(userId)) {
                            me = new UserHash(userFull.getUserId(), userFull.getFullName());
                        }
                        userHashes.add(new UserHash(userFull.getUserId(), userFull.getFullName()));
                    }

                    JsonObject content = new JsonObject();
                    content.put("message", me.getFullName() + " add " + userFulls.get(0).getFullName() + " to group");

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
                    Future<HashMap<String, Long>> increaseUnseenCountFuture = increaseUnseenCount(userFriendIds,
                            chatList.getSessionId());

                    CompositeFuture cp = CompositeFuture.all(insertChatMessageFuture, insertChatListFuture,
                            increaseUnseenCountFuture);

                    UserHash finalMe = me;
                    cp.setHandler(ar -> {
                        if (ar.succeeded()) {
                            NewChatSessionResponse newChatSessionResponse = new NewChatSessionResponse();
                            newChatSessionResponse.setType(IWsMessage.TYPE_CHAT_NEW_SESSION_RESPONSE);
                            newChatSessionResponse.setSessionId(chatMessage.getSessionId());
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

                            future.complete(true);
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

        return future;
    }

    public Future<JsonObject> rejectCall(RejectCallRequest rejectCallRequest, String userId) {
        Future<JsonObject> future = Future.future();
        Future<ChatList> getChatListBySessionIdFuture = getChatListBySessionId(rejectCallRequest.getSessionId());
        getChatListBySessionIdFuture.compose(chatList -> {
            RejectCallDTO rejectCallDTO = new RejectCallDTO();
            rejectCallDTO.setType(IWsMessage.REJECT_CAL);
            rejectCallDTO.setSessionId(rejectCallRequest.getSessionId());
            rejectCallDTO.setGroup(chatList.isGroup());
            for (UserHash userHash : chatList.getUserHashes()) {
                if (userHash.getUserId().equals(userId)) {
                    rejectCallDTO.setFullName(userHash.getFullName());
                }
            }
            if (rejectCallDTO.getFullName() == null) {
                JsonObject apiResponse = new JsonObject();
                apiResponse.put("success", true);
                apiResponse.put("code", 400);
                apiResponse.put("message", "You are not in that group!");
                apiResponse.put("payload", "");
                future.complete(apiResponse);
            } else {
                for (UserHash userhash : chatList.getUserHashes()) {
                    if (!userhash.getUserId().equals(userId)) {
                        userWsChannelManager.sendMessage(rejectCallDTO, userhash.getUserId());
                    }
                }

                JsonObject apiResponse = new JsonObject();
                apiResponse.put("success", true);
                apiResponse.put("code", 200);
                apiResponse.put("message", "Reject call successfully");
                apiResponse.put("payload", "");
                future.complete(apiResponse);
            }
        }, Future.future().setHandler(handler -> future.fail(handler.cause())));
        return future;
    }
}
