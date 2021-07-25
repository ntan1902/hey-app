package com.hey.handler.api;

import com.hey.manager.JwtManager;
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
        String path = StringUtils.substringAfter(requestPath, "/api/v1/systems");
        try {
            String jwt = jwtManager.getTokenFromRequest(request);

            JsonObject authObj = new JsonObject().put("jwtSystem", jwt);
            jwtManager.authenticate(authObj, event -> {
                if (event.succeeded()) {
                    String systemName = event.result().principal().getString("systemName");

                    JsonObject requestObject = null;
                    if (rc.getBody() != null && rc.getBody().length() > 0) {
                        requestObject = rc.getBodyAsJson();
                    }

                    switch (path) {
                        case "/createTransferMessage":
                            LogUtils.log("System  " + systemName + " request create transfer message");
                            createTransferMessage(response, requestObject);
                            break;


                    }
                } else {
                    throw new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message());
                }
            });
        } catch (HttpStatusException e) {
            handleUnauthorizedException(e, response);
        }
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
}
