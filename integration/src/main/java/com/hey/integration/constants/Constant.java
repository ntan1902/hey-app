package com.hey.integration.constants;

public class Constant {
    public final static String BASE_URL = "http://localhost:5050";
    public final static String REGISTER_URL = "/auth/api/v1/users/register";
    public final static String LOGIN_URL = "/auth/api/v1/users/login";
    public final static String CREATE_WALLET_URL = "/payment/api/v1/me/createWallet";
    public final static String TOP_UP_URL = "/payment/api/v1/me/topup";
    public final static String PAYLOAD = "payload";
    public final static String ACCESS_TOKEN = "accessToken";
    public final static String BANK_ID = "e8984aa8-b1a5-4c65-8c5e-036851ec783c";
    public final static Long AMOUNT = 2_000_000L;
    public final static String CREATE_PIN_URL = "/auth/api/v1/users/createPin";
}
