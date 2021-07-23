package com.hey.manager;

import com.hey.authentication.AuthService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.AsyncMap;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.User;

public class JwtManager {
    private SharedData sharedData;
    private AuthService authService;
    private static final String JWT_ASYNC_MAP = "jwt-async-map";


    public JwtManager(Vertx vertx) {

        this.sharedData = vertx.sharedData();

        authService = AuthService.getInstance();

    }

    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        String jwt = authInfo.getString("jwt");
        JsonObject jsonObject = new JsonObject().put("jwtUser", jwt);
        checkForExistingAsynMap(jwt).setHandler(event -> {
            if (event.result()) {
                resultHandler.handle(Future.failedFuture("Token has been blacklist"));
            } else {
                authService.authenticate(jsonObject, resultHandler);
            }
        });

    }

    private Future<Boolean> checkForExistingAsynMap(String token) {
        Future<Boolean> future = Future.future();
        sharedData.getAsyncMap(JWT_ASYNC_MAP, event -> {
            AsyncMap<Object, Object> aMap = event.result();
            aMap.get(token, event2 -> {
                if (event2.result() != null) {
                    future.complete(true);
                } else {
                    future.complete(false);
                }
            });
        });
        return future;
    }

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }

}
