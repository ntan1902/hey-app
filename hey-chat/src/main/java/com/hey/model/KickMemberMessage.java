package com.hey.model;

import java.io.Serializable;

public class KickMemberMessage extends WsMessage implements Serializable {
    private String memberId;
    private String sessionId;

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
