package com.hey.model;

import java.io.Serializable;

public class GetSessionIdResponse implements Serializable {
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
