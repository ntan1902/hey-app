package com.hey.model;

import java.util.List;

public class GetMembersOfSessionChatResponse {
    String owner;
    List<UserHash> members;

    public GetMembersOfSessionChatResponse(String owner, List<UserHash> members) {
        this.owner = owner;
        this.members = members;
    }

}
