package com.hey.handler.api;

import com.hey.repository.DataRepository;
import com.hey.service.APIService;
import com.hey.util.ErrorCode;
import com.hey.util.HeyHttpStatusException;
import com.hey.util.HttpStatus;
import com.hey.util.JsonUtils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.impl.HttpStatusException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseHandler {

    private static final Logger LOGGER = LogManager.getLogger(BaseHandler.class);

    protected APIService apiService;

    public void setApiService(APIService apiService) {
        this.apiService = apiService;
    }

    public APIService getApiService() {
        return apiService;
    }


    public void handleException(Throwable throwable, HttpServerResponse response) {

        if (throwable instanceof HeyHttpStatusException) {

            HeyHttpStatusException e = (HeyHttpStatusException) throwable;
            JsonObject obj = new JsonObject();
            obj.put("success", false);
            obj.put("code", e.getCode());
            obj.put("message", e.getPayload());
            obj.put("payload", "");
            response.setStatusCode(e.getStatusCode())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.encodePrettily(obj));
        } else if (throwable instanceof Exception) {
            Exception e = (Exception) throwable;
            LOGGER.error(e);
            e.printStackTrace();
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.code())
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(JsonUtils.toError500JSON());
        }
    }

    public void handleUnauthorizedException(HttpStatusException e, HttpServerResponse response) {
        JsonObject obj = new JsonObject();
        obj.put("success", false);
        obj.put("code", 401);
        obj.put("message", e.getPayload());
        obj.put("payload", "");
        response.setStatusCode(e.getStatusCode())
                .putHeader("content-type", "application/json; charset=utf-8")
                .end(JsonUtils.encodePrettily(obj));
    }
}
