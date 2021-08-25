package com.hey.integration.endpoint;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import java.nio.ByteBuffer;
import java.util.Map;

@ClientEndpoint
public class MyEndpoint {
    private Session userSession;

    @OnOpen
    public void onOpen(Session userSession) {
        System.out.println("opening websocket");
        this.userSession = userSession;
    }

    // text
    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println(message);
    }

    // binary
    @OnMessage
    public void onMessage(Session session, ByteBuffer message) {
        System.out.println("Buffer: " + message);
    }

    public void sendMessage(String message) {
        this.userSession.getAsyncRemote().sendText(message);
    }

    // @OnClose, @OnOpen, @OnError
}