package com.hey.model;

public class EditGroupNameRequest {
    private String sessionId;
    private String groupName;

    public EditGroupNameRequest(String groupName, String sessionId) {
        this.groupName = groupName;
        this.sessionId = sessionId;
    }

    public EditGroupNameRequest() {
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
