package com.hey.model.lucky;

public class ReceivedLuckyMoneyMessageRequest {
    private String sessionId;
    private long receiverId;
    private long luckyMoneyId;
    private long amount;
    private String message;
    private String createdAt;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(long receiverId) {
        this.receiverId = receiverId;
    }

    public long getLuckyMoneyId() {
        return luckyMoneyId;
    }

    public void setLuckyMoneyId(long luckyMoneyId) {
        this.luckyMoneyId = luckyMoneyId;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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
