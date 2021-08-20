package com.hey.model;

import java.io.Serializable;

public class JoinCallDTO extends WsMessage implements Serializable {
    private String peerId;

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }
}
