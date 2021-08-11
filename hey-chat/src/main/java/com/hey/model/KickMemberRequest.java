package com.hey.model;

public class KickMemberRequest {
    private String sessionId;
    private String memberId;

    public KickMemberRequest(String sessionId, String memberId) {
        this.sessionId = sessionId;
        this.memberId = memberId;
    }

    public KickMemberRequest() {
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }
}
