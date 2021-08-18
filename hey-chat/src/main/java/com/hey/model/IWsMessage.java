package com.hey.model;

public interface IWsMessage {
    String TYPE_CHAT_ITEMS_REQUEST = "CHAT_ITEMS_REQUEST";
    String TYPE_CHAT_ITEMS_RESPONSE = "CHAT_ITEMS_RESPONSE";
    String TYPE_CHAT_MESSAGE_REQUEST = "CHAT_MESSAGE_REQUEST";
    String TYPE_CHAT_MESSAGE_RESPONSE = "CHAT_MESSAGE_RESPONSE";
    String TYPE_CHAT_NEW_SESSION_RESPONSE = "CHAT_NEW_SESSION_RESPONSE";

    String TYPE_NOTIFICATION_FRIEND_ONLINE = "USER_ONLINE_RESPONSE";
    String TYPE_NOTIFICATION_FRIEND_OFFLINE = "USER_OFFLINE_RESPONSE";
    String TYPE_NOTIFICATION_ADD_FRIEND_RESPONSE = "ADD_FRIEND_RESPONSE";
    String TYPE_NOTIFICATION_ACCEPT_FRIEND_RESPONSE = "ACCEPT_FRIEND_RESPONSE";
    String TYPE_NOTIFICATION_TRANSFER_STATEMENT_RESPONSE = "TRANSFER_STATEMENT_RESPONSE";
}
