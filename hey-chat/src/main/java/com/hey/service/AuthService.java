package com.hey.service;

import com.hey.model.EditProfileRequest;
import com.hey.util.PropertiesUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.impl.JWTUser;
import io.vertx.ext.web.client.WebClient;

public class AuthService implements AuthProvider {
    private WebClient webClient;
    private static AuthService instance;
    private String jwt;

    public static void createInstance(WebClient webClient, String jwt) {
        instance = new AuthService();
        instance.jwt = jwt;
        instance.webClient = webClient;
    }

    public static AuthService getInstance() {
        return instance;
    }

    @Override
    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        Integer port = PropertiesUtils.getInstance().getIntValue("auth.port");
        String host = PropertiesUtils.getInstance().getValue("auth.host");

        String baseURL = PropertiesUtils.getInstance().getValue("auth.baseurl");
        String url = authInfo.containsKey("jwtUser") ? baseURL + "/authorizeUser"
                : authInfo.containsKey("jwtSystem") ? baseURL + "/authorizeSystem" : "";

        webClient.post(port, host, url).putHeader("Authorization", jwt).sendJsonObject(authInfo,
                httpResponseAsyncResult -> {
                    if (httpResponseAsyncResult.succeeded()) {
                        JsonObject result = httpResponseAsyncResult.result().bodyAsJsonObject();
                        if (result.getInteger("code") == 400) {
                            resultHandler.handle(Future.failedFuture("Unauthorized"));
                        } else {
                            JsonObject payload = result.getJsonObject("payload");

                            if (payload.containsKey("userId")) {
                                JsonObject user = new JsonObject();
                                user.put("userId", payload.getString("userId"));
                                resultHandler.handle(Future.succeededFuture(new JWTUser(user, "permissions")));
                            } else if (payload.containsKey("systemId")) {
                                JsonObject system = new JsonObject();
                                system.put("systemId", payload.getString("systemId"));
                                resultHandler.handle(Future.succeededFuture(new JWTUser(system, "permissions")));
                            }
                        }
                    } else {
                        resultHandler.handle(Future.failedFuture("Can't auth!!!"));
                    }
                });

    }

    public void editProfile(EditProfileRequest editProfileRequest, String userId, Handler<AsyncResult<JsonObject>> resultHandler) {
        Integer port = PropertiesUtils.getInstance().getIntValue("auth.port");
        String host = PropertiesUtils.getInstance().getValue("auth.host");

        String baseURL = PropertiesUtils.getInstance().getValue("auth.baseurl");
        String url = baseURL + "/editProfile/" + userId;

        webClient.patch(port, host, url).putHeader("Authorization", jwt).sendJson(editProfileRequest,
                httpResponseAsyncResult -> {
                    if (httpResponseAsyncResult.succeeded()) {
                        JsonObject result = httpResponseAsyncResult.result().bodyAsJsonObject();
                        if (result.getInteger("code") == 400) {
                            resultHandler.handle(Future.failedFuture(result.getString("message")));
                        } else {
                            resultHandler.handle(Future.succeededFuture(result));
                        }
                    } else {
                        resultHandler.handle(Future.failedFuture("BAD REQUEST"));
                    }
                });
    }
}