package com.hey.handler.api;

import com.hey.manager.JwtManager;
import com.hey.model.lucky.LuckyMoneyMessageRequest;
import com.hey.model.lucky.ReceiveLuckyMoneyMessageRequest;
import com.hey.model.lucky.UserIdSessionIdRequest;
import com.hey.model.lucky.UserIdSessionIdResponse;
import com.hey.model.payment.TransferMessageRequest;
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

public class SystemApiHandler extends BaseHandler {
    public JwtManager jwtManager;

    public SystemApiHandler() {
    }

    public JwtManager getJwtManager() {
        return jwtManager;
    }

    public void setJwtManager(JwtManager jwtManager) {
        this.jwtManager = jwtManager;
    }

    public void handle(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String requestPath = request.path();
        String path = StringUtils.substringAfter(requestPath, "/chat/api/v1/systems");

        String jwt = jwtManager.getTokenFromRequest(request);

        JsonObject authObj = new JsonObject().put("jwtSystem", jwt);
        jwtManager.authenticate(authObj, event -> {
            if (event.succeeded()) {
                String systemId = event.result().principal().getString("systemId");

                JsonObject requestObject = null;
                if (rc.getBody() != null && rc.getBody().length() > 0) {
                    requestObject = rc.getBodyAsJson();
                }

                switch (path) {
                    case "/createTransferMessage":
                        LogUtils.log("System  " + systemId + " request create transfer message");
                        createTransferMessage(response, requestObject);
                        break;
                    case "/createLuckyMoneyMessage":
                        LogUtils.log("System  " + systemId + " request create lucky money message");
                        createLuckyMoneyMessage(response, requestObject);
                        break;
                    case "/isUserExistInSession":
                        LogUtils.log("System  " + systemId + " request check whether user in session");
                        checkUserIdExistInSessionId(response, requestObject);
                        break;
                    case "/receiveLuckyMoneyMessage":
                        LogUtils.log("System  " + systemId + " request receive lucky money");
                        receiveLuckyMoneyMessage(response, requestObject);
                        break;

                }
            } else {
                handleUnauthorizedException(new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message()), response);
            }
        });

    }

    private void createLuckyMoneyMessage(HttpServerResponse response, JsonObject requestObject) {
        LuckyMoneyMessageRequest luckyMoneyMessageRequest = requestObject.mapTo(LuckyMoneyMessageRequest.class);
        Future<Boolean> successFuture = apiService.createLuckyMoneyMessage(luckyMoneyMessageRequest);

        successFuture.compose(success -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(success.toString()));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void createTransferMessage(HttpServerResponse response, JsonObject requestObject) {
        TransferMessageRequest transferMessageRequest = requestObject.mapTo(TransferMessageRequest.class);

        Future<Boolean> successFuture = apiService.transferMessage(transferMessageRequest);

        successFuture.compose(success -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(success.toString()));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

    private void checkUserIdExistInSessionId(HttpServerResponse response, JsonObject requestObject) {
        UserIdSessionIdRequest request = requestObject.mapTo(UserIdSessionIdRequest.class);
        Future<UserIdSessionIdResponse> successFuture = apiService.checkUserExistInSession(request);

        successFuture.compose(success -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(success));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }


    private void receiveLuckyMoneyMessage(HttpServerResponse response, JsonObject requestObject) {
        ReceiveLuckyMoneyMessageRequest request = requestObject.mapTo(ReceiveLuckyMoneyMessageRequest.class);

        Future<Boolean> successFuture = apiService.receiveLuckyMoneyMessage(request);

        successFuture.compose(success -> {
            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(success.toString()));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));
    }

}
