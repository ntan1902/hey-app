package com.hey.model;

import java.io.Serializable;

public class TransferMessageResponse extends WsMessage implements Serializable {
    TransferMessageContent content;

    public TransferMessageContent getContent() {
        return content;
    }

    public void setContent(TransferMessageContent content) {
        this.content = content;
    }
}
