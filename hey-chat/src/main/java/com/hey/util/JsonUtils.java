package com.hey.util;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;

import java.util.List;

public final class JsonUtils {
    private JsonUtils() {
    }

    public static String toSuccessJSON(Object message) {
        JsonObject objectResult = new JsonObject(Json.encodePrettily(message));
        JsonObject object = new JsonObject();
        object.put("success", true);
        object.put("code", HttpStatus.OK.code());
        object.put("message", "");
        object.put("payload", objectResult);
        return Json.encodePrettily(object);
    }

    public static String toSuccessJSON(String message) {
        JsonObject object = new JsonObject();
        object.put("success", true);
        object.put("code", HttpStatus.OK.code());
        object.put("message", message);
        object.put("payload", "");
        return Json.encodePrettily(object);
    }

    public static String encodePrettily(Object error){
        return Json.encodePrettily(error);
    }

    public static String toError500JSON() {
        JsonObject object = new JsonObject();
        object.put("success", false);
        object.put("code", HttpStatus.INTERNAL_SERVER_ERROR.code());
        object.put("message", "Oops, The handler was unable to complete your request. We will be back soon :(");
        object.put("payload", "");
        return Json.encodePrettily(object);
    }
}
