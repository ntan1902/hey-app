package com.hey.model;

import java.io.Serializable;
import java.util.Date;

public class ChatMessageResponse extends WsMessage implements Serializable {

    private String sessionId;
    private String userId;
    private String name;
    private String message;
    private Date createdDate;
    private boolean isTransferStatement;
    private boolean isChangeGroupName;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isTransferStatement() {
        return isTransferStatement;
    }

    public void setTransferStatement(boolean transferStatement) {
        isTransferStatement = transferStatement;
    }

    public boolean isChangeGroupName() {
        return isChangeGroupName;
    }

    public void setChangeGroupName(boolean changeGroupName) {
        isChangeGroupName = changeGroupName;
    }
}
