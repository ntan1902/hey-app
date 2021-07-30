package com.hey.handler.api;

import com.hey.model.User;
import com.hey.service.APIService;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import com.hey.util.LogUtils;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PublicApiHandler extends BaseHandler{
    public void handle(RoutingContext rc) {
        HttpServerRequest request = rc.request();
        HttpServerResponse response = rc.response();
        String requestPath = request.path();
        String path = StringUtils.substringAfter(requestPath, "/chat/api/public");
        String json = rc.getBodyAsString();

        switch (path) {
            case "/register":
                LogUtils.log("New register request");
                registerUser(response, json);
                break;
            default:
                response.end();
                break;
        }

    }

    public void registerUser(HttpServerResponse response, String jsonData) {

        Future<User> registerUserFuture = apiService.registerUser(jsonData);

        registerUserFuture.compose(user -> {

            response
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(user));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), response);
        }));

    }

    public void initTestData(RoutingContext routingContext) {

        Future<JsonObject> futureInitTestData = apiService.initTestData();

        futureInitTestData.compose(jsonObject -> {

            routingContext.response()
                    .setStatusCode(HttpStatus.OK.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toSuccessJSON(jsonObject));

        }, Future.future().setHandler(handler -> {
            handleException(handler.cause(), routingContext.response());
        }));

    }
}
