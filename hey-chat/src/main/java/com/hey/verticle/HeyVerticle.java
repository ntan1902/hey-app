package com.hey.verticle;

import com.hey.api.ApiServer;
import com.hey.api.WsServer;
import com.hey.authentication.AuthService;
import com.hey.cache.client.RedisCacheClient;
import com.hey.handler.ProtectedApiHandler;
import com.hey.handler.PublicApiHandler;
import com.hey.handler.WsHandler;
import com.hey.manager.JwtManager;
import com.hey.manager.UserWsChannelManager;
import com.hey.repository.DataRepository;
import com.hey.service.APIService;
import com.hey.util.PropertiesUtils;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.redis.RedisClient;
import io.vertx.redis.RedisOptions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HeyVerticle extends AbstractVerticle {

    private static final Logger LOGGER = LogManager.getLogger(HeyVerticle.class);
    private ApiServer apiServer;
    private WsServer wsServer;

    public ApiServer getApiServer() {
        return apiServer;
    }

    public WsServer getWebsocketServer() {
        return wsServer;
    }

    @Override
    public void start(Future<Void> future) {
        LOGGER.info("{} verticle {} start", deploymentID(), Thread.currentThread().getName());

        // Create a JWT Auth Provider
        LOGGER.info("Initial JWT for verticle {}", Thread.currentThread().getName());

        WebClient webClient = WebClient.create(vertx);
        JsonObject loginInfo = new JsonObject();
        loginInfo.put("systemName", "chat");
        loginInfo.put("systemKey", "123456");
        webClient.post(
                PropertiesUtils.getInstance().getIntValue("auth.port"),
                PropertiesUtils.getInstance().getValue("auth.host"),
                PropertiesUtils.getInstance().getValue("auth.baseurl") + "/login"
        ).sendJsonObject(loginInfo, httpResponseAsyncResult -> {
            JsonObject payload = httpResponseAsyncResult.result().bodyAsJsonObject().getJsonObject("payload");
            String tokenType = payload.getString("tokenType");
            AuthService.createInstance(webClient, tokenType + " " + payload.getString("accessToken"));


            JwtManager jwtManager = new JwtManager(vertx);

            //Inject dependency
            LOGGER.info("Starting Inject Dependency for verticle {}", Thread.currentThread().getName());
            RedisClient client = RedisClient.create(vertx,
                    new RedisOptions().setHost(PropertiesUtils.getInstance().getValue("redis.host")));
            DataRepository repository = new RedisCacheClient(client);

            // User Channel Manager
            UserWsChannelManager userWsChannelManager = new UserWsChannelManager();
            userWsChannelManager.setEventBus(vertx.eventBus());
            userWsChannelManager.setSharedData(vertx.sharedData());

            // API Service
            APIService apiService = new APIService();
            apiService.setDataRepository(repository);
            apiService.setUserWsChannelManager(userWsChannelManager);


            // Protected API Handler
            ProtectedApiHandler protectedApiHandler = new ProtectedApiHandler();
            protectedApiHandler.setDataRepository(repository);
            protectedApiHandler.setJwtManager(jwtManager);
            protectedApiHandler.setApiService(apiService);

            // Public API Handler
            PublicApiHandler publicApiHandler = new PublicApiHandler();
            publicApiHandler.setDataRepository(repository);
            publicApiHandler.setApiService(apiService);


            // Web Socket Handler
            WsHandler wsHandler = new WsHandler();
            wsHandler.setDataRepository(repository);
            wsHandler.setApiService(apiService);
            wsHandler.setUserWsChannelManager(userWsChannelManager);

            // API Server
            this.apiServer = ApiServer.newInstance();
            apiServer.setProtectedApiHandler(protectedApiHandler);
            apiServer.setPublicApiHandler(publicApiHandler);

            // Web Socket Server
            this.wsServer = WsServer.newInstance();
            wsServer.setWsHandler(wsHandler);
            wsServer.setUserWsChannelManager(userWsChannelManager);
            wsServer.setJwtManager(jwtManager);

            LOGGER.info("Inject Dependency successfully for verticle {}", Thread.currentThread().getName());

            Future.succeededFuture()
                    .compose(v -> apiServer.createHttpServer(vertx))
                    .compose(v -> wsServer.createWsServer(vertx))
                    .setHandler(future);
        });
    }

    @Override
    public void stop() {
        LOGGER.info("Shutting down application");
    }
}
