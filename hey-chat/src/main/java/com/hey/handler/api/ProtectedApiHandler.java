package com.hey.handler.api;

import com.hey.manager.JwtManager;
import com.hey.model.*;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import com.hey.util.LogUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import org.apache.commons.lang3.StringUtils;

public class ProtectedApiHandler extends BaseHandler {
    public JwtManager jwtManager;

    public void setJwtManager(JwtManager jwtManager) {
        this.jwtManager = jwtManager;
    }


    public void handle(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String requestPath = request.path();
        String path = StringUtils.substringAfter(requestPath, "/chat/api/protected");

        String jwt = jwtManager.getTokenFromRequest(request);

        JsonObject authObj = new JsonObject().put("jwtUser", jwt);
        jwtManager.authenticate(authObj, event -> {
            if (event.succeeded()) {
                String userId = event.result().principal().getString("userId");

                JsonObject requestObject = null;
                if (rc.getBody() != null && rc.getBody().length() > 0) {
                    requestObject = rc.getBodyAsJson();
                }

                switch (path) {
                    case "/ping":
                        ping(response);
                        break;
                    case "/chatlist":
                        LogUtils.log("User  " + userId + " request chat list");
                        getChatList(response, requestObject, userId);
                        break;
                    case "/addressbook":
                        LogUtils.log("User  " + userId + " request address book");
                        getAddressBook(response, requestObject, userId);
                        break;
                    case "/waitingfriend":
                        LogUtils.log("User  " + userId + " is waiting friend");
                        getWaitingFriends(response, requestObject, userId);
                        break;
                    case "/closewaitingfriend":
                        LogUtils.log("User  " + userId + " closes waiting friend");
                        deleteWaitingFriend(response, requestObject, userId);
                        break;
                    case "/usernameexisted":
                        LogUtils.log("User  " + userId + " check username " + requestObject);
                        checkUsernameExisted(response, requestObject, userId);
                        break;
                    case "/sessionidbyuserid":
                        LogUtils.log("User  " + userId + " get session id " + requestObject);
                        getSessionIdByUserId(response, requestObject, userId);
                        break;
                    case "/waitingchatheader":
                        LogUtils.log("User  " + userId + " get temporarily chat header " + requestObject);
                        waitingChatHeader(response, requestObject, userId);
                        break;
                    case "/addfriend":
                        LogUtils.log("User  " + userId + " add new friend " + requestObject);
                        addFriend(response, requestObject, userId);
                        break;
                    case "/status":
                        LogUtils.log("User  " + userId + " change status " + requestObject);
                        changeStatus(response, requestObject, userId);
                        break;
                    case "/user":
                        LogUtils.log("User  " + userId + " get profile " + requestObject);
                        getUserProfile(response, requestObject, userId);
                        break;
                    case "/editprofile":
                        LogUtils.log("User  " + userId + " edit profile " + requestObject);
                        editProfile(response, requestObject, userId);
                        break;
                    case "/editgroupname":
                        LogUtils.log("User  " + userId + " edit group name " + requestObject);
                        editGroupName(response, requestObject, userId);
                        break;
                    case "/kickmember":
                        LogUtils.log("User  " + userId + " kick member " + requestObject);
                        kickMember(response, requestObject, userId);
                        break;
                    case "/outgroup":
                        LogUtils.log("User  " + userId + " out group " + requestObject);
                        outGroup(response, requestObject, userId);
                        break;
                    case "/getUserOfSessionChat":
                        LogUtils.log("User " + userId + " get members of session " + requestObject);
                        getUserOfSessionChat(response, requestObject, userId);
                        break;
                    case "/addfriendrequest":
                        LogUtils.log("User " + userId + " send add friend request to " + requestObject);
                        addFriendRequest(response, requestObject, userId);
                        break;
                    case "/addfriendtosession":
                        LogUtils.log("User " + userId + " add friend to session " + requestObject);
                        addFriendToSession(response, requestObject, userId);
                        break;
                    case "/makeCall":
                        LogUtils.log("User " + userId + " make call " + requestObject);
                        makeCall(response, requestObject, userId);
                    case "/getICEServer":
                        LogUtils.log("User " + userId + " get ICEServer");
                        getICEServer(response, userId);
                    case "/joinCall":
                        LogUtils.log("User " + userId + " join call " + requestObject);
                        joinCall(response, requestObject, userId);
                    case "/rejectCall":
                        LogUtils.log("User " + userId + " reject call " + requestObject);
                        rejectCall(response,requestObject,userId);
                }
            } else {
                handleUnauthorizedException(new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message()), response);
            }
        });
    }

    private void rejectCall(HttpServerResponse response, JsonObject requestObject, String userId) {
        RejectCallRequest rejectCallRequest = requestObject.mapTo(RejectCallRequest.class);
        Future<JsonObject> rejectCallFuture = apiService.rejectCall(rejectCallRequest, userId);

        rejectCallFuture.compose(makeCallResponse -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(makeCallResponse.encodePrettily());

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void joinCall(HttpServerResponse response, JsonObject requestObject, String userId) {
        JoinCallRequest joinCallRequest = requestObject.mapTo(JoinCallRequest.class);
        Future<JsonObject> makeCallFuture = apiService.joinCall(joinCallRequest, userId);

        makeCallFuture.compose(makeCallResponse -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(makeCallResponse.encodePrettily());

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void getICEServer(HttpServerResponse response, String userId) {
        Future<JsonObject> getICEServer = apiService.getICEServer();
        getICEServer.compose(apiResponse -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(apiResponse.encodePrettily());
        }, Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

    private void makeCall(HttpServerResponse response, JsonObject requestObject, String userId) {
        MakeCallRequest makeCallRequest = requestObject.mapTo(MakeCallRequest.class);
        Future<JsonObject> makeCallFuture = apiService.makeCall(makeCallRequest, userId);

        makeCallFuture.compose(makeCallResponse -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(makeCallResponse.encodePrettily());

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    public void ping(HttpServerResponse response) {
        response
                .setStatusCode(HttpStatus.OK.code())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toSuccessJSON("Pong"));
    }

    public void getChatList(HttpServerResponse response, JsonObject requestObject, String userId) {

        Future<ChatListResponse> getChatListFuture = apiService.getChatList(userId);

        getChatListFuture.compose(chatListResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(chatListResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));

    }


    public void getAddressBook(HttpServerResponse response, JsonObject requestObject, String userId) {

        Future<AddressBookResponse> getAddressBookFuture = apiService.getAddressBook(userId);

        getAddressBookFuture.compose(addressBookResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(addressBookResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    public void getWaitingFriends(HttpServerResponse response, JsonObject requestObject, String userId) {

        Future<AddressBookResponse> getAddressBookFuture = apiService.getWaitingFriends(userId);

        getAddressBookFuture.compose(addressBookResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(addressBookResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    public void deleteWaitingFriend(HttpServerResponse response, JsonObject requestObject, String userId) {
        GetSessionIdRequest getSessionIdRequest = requestObject.mapTo(GetSessionIdRequest.class);

        Future<JsonObject> getAddressBookFuture = apiService.deleteWaitingFriend(getSessionIdRequest, userId);

        getAddressBookFuture.compose(addressBookResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON("OK"));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    public void checkUsernameExisted(HttpServerResponse response, JsonObject requestObject, String userId) {
        UsernameExistedRequest usernameExistedRequest = requestObject.mapTo(UsernameExistedRequest.class);

        Future<UsernameExistedResponse> checkUsernameExistedFuture = apiService.checkUsernameExisted(usernameExistedRequest, userId);

        checkUsernameExistedFuture.compose(usernameExistedResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(usernameExistedResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    public void getSessionIdByUserId(HttpServerResponse response, JsonObject requestObject, String userId) {
        GetSessionIdRequest getSessionIdRequest = requestObject.mapTo(GetSessionIdRequest.class);

        Future<GetSessionIdResponse> getSessionIdByUserIdFuture = apiService.getSessionIdByUserId(getSessionIdRequest, userId);

        getSessionIdByUserIdFuture.compose(getSessionIdResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(getSessionIdResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    public void waitingChatHeader(HttpServerResponse response, JsonObject requestObject, String userId) {
        WaitingChatHeaderRequest waitingChatHeaderRequest = requestObject.mapTo(WaitingChatHeaderRequest.class);

        Future<WaitingChatHeaderResponse> waitingChatHeaderFuture = apiService.waitingChatHeader(waitingChatHeaderRequest, userId);

        waitingChatHeaderFuture.compose(waitingChatHeaderResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(waitingChatHeaderResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));

    }

    public void addFriend(HttpServerResponse response, JsonObject requestObject, String userId) {
        AddFriendRequest addFriendRequest = requestObject.mapTo(AddFriendRequest.class);

        Future<AddFriendResponse> addFriendFuture = apiService.addFriend(addFriendRequest, userId);

        addFriendFuture.compose(addFriendResponse -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(addFriendResponse));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));

    }

    public void changeStatus(HttpServerResponse response, JsonObject requestObject, String userId) {
        ChangeStatusRequest changeStatusRequest = requestObject.mapTo(ChangeStatusRequest.class);

        Future<JsonObject> insertUserStatusFuture = apiService.changeStatus(changeStatusRequest, userId);

        insertUserStatusFuture.compose(jsonObject -> response
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toSuccessJSON(jsonObject)), Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));


    }

    public void getUserProfile(HttpServerResponse response, JsonObject requestObject, String userId) {
        Future<UserProfileResponse> getUserProfileFuture = apiService.getUserProfile(userId);

        getUserProfileFuture.compose(userProfileResponse -> response
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.toSuccessJSON(userProfileResponse)), Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }


    private void editProfile(HttpServerResponse response, JsonObject requestObject, String userId) {
        EditProfileRequest editProfileRequest = requestObject.mapTo(EditProfileRequest.class);
        Future<JsonObject> editProfileFuture = apiService.editProfile(editProfileRequest, userId);

        editProfileFuture.compose(editProfile -> response
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(editProfile.encodePrettily()), Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void editGroupName(HttpServerResponse response, JsonObject requestObject, String userId) {
        EditGroupNameRequest editGroupNameRequest = requestObject.mapTo(EditGroupNameRequest.class);
        Future<JsonObject> editGroupNameFuture = apiService.editGroupName(editGroupNameRequest, userId);

        editGroupNameFuture.compose(editGroupName -> response
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(editGroupName.encodePrettily()), Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void kickMember(HttpServerResponse response, JsonObject requestObject, String userId) {
        KickMemberRequest kickMemberRequest = requestObject.mapTo(KickMemberRequest.class);
        Future<JsonObject> kickMemberFuture = apiService.kickMember(kickMemberRequest, userId);

        kickMemberFuture.compose(kickMember -> response
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(kickMember.encodePrettily()), Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void outGroup(HttpServerResponse response, JsonObject requestObject, String userId) {
        OutGroupRequest outGroupRequest = requestObject.mapTo(OutGroupRequest.class);
        Future<JsonObject> outGroupFuture = apiService.outGroup(outGroupRequest, userId);

        outGroupFuture.compose(outGroup -> response
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(outGroup.encodePrettily()), Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }


    private void getUserOfSessionChat(HttpServerResponse response, JsonObject requestObject, String userId) {
        GetMembersOfSessionRequest getMembersOfSessionRequest = requestObject.mapTo(GetMembersOfSessionRequest.class);
        Future<JsonObject> getMembersOfSessionFuture = apiService.getMembersOfSessionChat(userId, getMembersOfSessionRequest.getSessionId());

        getMembersOfSessionFuture.compose(members -> {
            response.putHeader("content-type", "application/json; charset=utf-8")
                    .end(members.encodePrettily());
        }, Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }


    private void addFriendRequest(HttpServerResponse response, JsonObject requestObject, String userId) {
        AddFriendRequest request = requestObject.mapTo(AddFriendRequest.class);

        Future<AddFriendResponse> addWaitingFriendFuture = apiService.addWaitingFriend(request, userId);

        addWaitingFriendFuture.compose(addWaitingFriendResponse -> {
            response.putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(addWaitingFriendResponse));
        }, Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }


    private void addFriendToSession(HttpServerResponse response, JsonObject requestObject, String userId) {
        AddFriendToSessionRequest request = requestObject.mapTo(AddFriendToSessionRequest.class);

        Future<Boolean> addFriendToSessionFuture = apiService.addFriendToSessionRequest(request, userId);

        addFriendToSessionFuture.compose(res -> {
            response.putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(res.toString()));
        }, Future.future().setHandler(handler -> handleException(handler.cause(), response)));
    }

}
