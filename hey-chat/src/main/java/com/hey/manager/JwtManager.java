package com.hey.manager;

import com.hey.auth.AuthService;
import com.hey.util.HttpStatus;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.json.JsonObject;
import io.vertx.core.shareddata.SharedData;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import org.apache.commons.lang3.StringUtils;

public class JwtManager {
    private SharedData sharedData;
    private AuthService authService;
    public static final String AUTHENTICATION_SCHEME = "Bearer";


    public JwtManager(Vertx vertx) {

        this.sharedData = vertx.sharedData();

        authService = AuthService.getInstance();

    }

    public void authenticate(JsonObject authInfo, Handler<AsyncResult<User>> resultHandler) {
        authService.authenticate(authInfo, resultHandler);
    }

    public String getTokenFromRequest(HttpServerRequest request) {
        String authorization = request.headers().get(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isBlank(authorization)) {
            throw new HttpStatusException(HttpStatus.UNAUTHORIZED.code(), HttpStatus.UNAUTHORIZED.message());
        }
        return authorization.replace(AUTHENTICATION_SCHEME, "").trim();
    }

    public void setSharedData(SharedData sharedData) {
        this.sharedData = sharedData;
    }

}
