package com.hey.lucky.api;

import com.hey.lucky.dto.payment_service.CreateTransferFromUserRequest;
import com.hey.lucky.dto.payment_service.CreateTransferFromUserResponse;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;

public interface PaymentApi {
    GetAllWalletsResponse getAllWallets();
    CreateTransferFromUserResponse createTransferFromUser(CreateTransferFromUserRequest request);
}
