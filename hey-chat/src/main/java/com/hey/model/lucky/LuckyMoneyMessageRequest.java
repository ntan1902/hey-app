package com.hey.model.lucky;

public class LuckyMoneyMessageRequest {
    private String userId;
    private String sessionId;
    private Long luckyMoneyId;
    private String message;
    private String createdAt;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getLuckyMoneyId() {
        return luckyMoneyId;
    }

    public void setLuckyMoneyId(Long luckyMoneyId) {
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
