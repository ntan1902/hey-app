package com.hey.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ChatList implements Serializable {
    private List<UserHash> userHashes;
    private String sessionId;
    private Date updatedDate;
    private String lastMessage;
    private boolean isGroup;
    private String groupName;
    private String owner;


    public List<UserHash> getUserHashes() {
        return userHashes;
    }

    public void setUserHashes(List<UserHash> userHashes) {
        this.userHashes = userHashes;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean getGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

}
