package com.hey.model.lucky;

public class ReceiveLuckyMoneyMessageRequest {
    private String sessionId;
    private Long receiverId;
    private Long luckyMoneyId;
    private Long amount;
    private String message;
    private String createdAt;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getLuckyMoneyId() {
        return luckyMoneyId;
    }

    public void setLuckyMoneyId(Long luckyMoneyId) {
        this.luckyMoneyId = luckyMoneyId;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
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
