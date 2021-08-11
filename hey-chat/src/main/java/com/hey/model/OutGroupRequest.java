package com.hey.model;

public class OutGroupRequest {
    private String sessionId;

    public OutGroupRequest(String sessionId) {
        this.sessionId = sessionId;
    }

    public OutGroupRequest() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
