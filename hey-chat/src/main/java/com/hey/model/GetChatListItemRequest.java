package com.hey.model;

import java.io.Serializable;

public class GetChatListItemRequest implements Serializable {
    private String sessionId;

    public GetChatListItemRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public GetChatListItemRequest() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
