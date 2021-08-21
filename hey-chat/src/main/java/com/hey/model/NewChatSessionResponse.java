package com.hey.model;

import java.io.Serializable;

public class NewChatSessionResponse extends WsMessage implements Serializable {

    private String sessionId;
    private boolean isTransferStatement;
    private boolean isChangeGroupName;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
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
