package com.hey.model;

public class MakeCallRequest {
    private String sessionId;
    private boolean isVideoCall;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean getIsVideoCall() {
        return isVideoCall;
    }

    public void setIsVideoCall(boolean isVideoCall) {
        this.isVideoCall = isVideoCall;
    }
}
