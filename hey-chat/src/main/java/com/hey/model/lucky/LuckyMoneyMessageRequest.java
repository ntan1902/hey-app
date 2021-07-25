package com.hey.model.lucky;

public class LuckyMoneyMessageRequest {
    private long userId;
    private long sessionId;
    private long luckyMoneyId;
    private String message;
    private String createdAt;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public long getLuckyMoneyId() {
        return luckyMoneyId;
    }

    public void setLuckyMoneyId(long luckyMoneyId) {
        this.luckyMoneyId = luckyMoneyId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
