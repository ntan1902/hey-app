package com.hey.model;

import java.io.Serializable;

public class HaveCallDTO extends WsMessage implements Serializable {
    private String sessionId;
    private boolean isVideoCall;
    private String groupName;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isVideoCall() {
        return isVideoCall;
    }

    public void setVideoCall(boolean videoCall) {
        isVideoCall = videoCall;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
