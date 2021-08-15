package com.hey.model;

import java.io.Serializable;

public class WaitingChatHeaderRequest implements Serializable {
    private String[] usernames;
    private String groupName;

    public String[] getUsernames() {
        return usernames;
    }

    public void setUsernames(String[] usernames) {
        this.usernames = usernames;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
