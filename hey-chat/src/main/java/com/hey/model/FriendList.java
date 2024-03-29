package com.hey.model;

import java.io.Serializable;

public class FriendList implements Serializable {

    private UserHash currentUserHashes;
    private UserHash friendUserHashes;

    public UserHash getCurrentUserHashes() {
        return currentUserHashes;
    }

    public void setCurrentUserHashes(UserHash currentUserHashes) {
        this.currentUserHashes = currentUserHashes;
    }

    public UserHash getFriendUserHashes() {
        return friendUserHashes;
    }

    public void setFriendUserHashes(UserHash friendUserHashes) {
        this.friendUserHashes = friendUserHashes;
    }
}
