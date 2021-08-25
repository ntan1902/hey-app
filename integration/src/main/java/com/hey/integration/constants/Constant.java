package com.hey.integration.constants;

public class Constant {
    public final static String BASE_URL = "http://localhost:5050";
    public final static String REGISTER_URL = "/auth/api/v1/users/register";
    public final static String LOGIN_URL = "/auth/api/v1/users/login";
    public final static String LOGOUT_URL = "/auth/api/v1/users/logout";
    public final static String CREATE_WALLET_URL = "/payment/api/v1/me/createWallet";
    public final static String TOP_UP_URL = "/payment/api/v1/me/topup";
    public final static String BANK_ID = "e8984aa8-b1a5-4c65-8c5e-036851ec783c";
    public final static Long AMOUNT = 4_000_000L;
    public final static String CREATE_PIN_URL = "/auth/api/v1/users/createPin";
    public final static String CREATE_TRANSFER_URL = "/payment/api/v1/me/createTransfer";
    public final static String CREATE_SOFT_TOKEN_URL = "/auth/api/v1/users/createSoftTokenByPin";
    public final static String ADD_FRIEND_REQUEST_URL = "/chat/api/protected/addfriendrequest";
    public final static String ACCEPT_FRIEND_REQUEST_URL = "/chat/api/protected/addfriend";
    public final static String CLOSE_FRIEND_REQUEST_URL = "/chat/api/protected/closewaitingfriend";
}
