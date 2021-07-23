package com.hey.authentication;

import com.hey.util.PropertiesUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.auth.jwt.impl.JWTUser;
import io.vertx.ext.web.client.WebClient;

public class AuthService {
    private WebClient webClient;
    private static AuthService instance;
    private String jwtSystem;

    public static void createInstance(WebClient webClient, String jwtSystem) {
        instance = new AuthService();
        instance.jwtSystem = jwtSystem;
        instance.webClient = webClient;
    }

    public static AuthService getInstance() {
        return instance;
    }

    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        webClient.post(
                PropertiesUtils.getInstance().getIntValue("auth.port"),
                PropertiesUtils.getInstance().getValue("auth.host"),
                PropertiesUtils.getInstance().getValue("auth.baseurl") + "/authorizeUser"
        ).putHeader("Authorization", jwtSystem).sendJsonObject(authInfo, httpResponseAsyncResult -> {
            if (httpResponseAsyncResult.succeeded()) {
                JsonObject result = httpResponseAsyncResult.result().bodyAsJsonObject();
                if (result.getInteger("code") == 401) {
                    resultHandler.handle(Future.failedFuture("Unauthorize"));
                }
                JsonObject payload = result.getJsonObject("payload");
                if (payload.containsKey("userId")) {
                    JsonObject user = new JsonObject();
                    user.put("userId", payload.getInteger("userId").toString());
                    resultHandler.handle(Future.succeededFuture(new JWTUser(user, "permissions")));
                }
            } else {
                resultHandler.handle(Future.failedFuture("Can't auth!!!"));
            }
        });

    }

//    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
//        try {
//            JsonObject payload = this.jwt.decode(authInfo.getString("jwt"));
//            if (this.jwt.isExpired(payload, this.jwtOptions)) {
//                resultHandler.handle(Future.failedFuture("Expired JWT token."));
//                return;
//            }
//
//            if (this.jwtOptions.getAudience() != null) {
//                JsonArray target;
//                if (payload.getValue("aud") instanceof String) {
//                    target = (new JsonArray()).add(payload.getValue("aud", ""));
//                } else {
//                    target = payload.getJsonArray("aud", EMPTY_ARRAY);
//                }
//
//                if (Collections.disjoint(this.jwtOptions.getAudience(), target.getList())) {
//                    resultHandler.handle(Future.failedFuture("Invalid JWT audient. expected: " + Json.encode(this.jwtOptions.getAudience())));
//                    return;
//                }
//            }
//
//            if (this.jwtOptions.getIssuer() != null && !this.jwtOptions.getIssuer().equals(payload.getString("iss"))) {
//                resultHandler.handle(Future.failedFuture("Invalid JWT issuer"));
//                return;
//            }
//
//            resultHandler.handle(Future.succeededFuture(new JWTUser(payload, this.permissionsClaimKey)));
//        } catch (RuntimeException var5) {
//            resultHandler.handle(Future.failedFuture(var5));
//        }
//
//    }

}