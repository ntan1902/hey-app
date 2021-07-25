package com.hey.lucky.api;

import com.hey.lucky.dto.payment_service.*;

public interface PaymentApi {
    GetAllWalletsResponse getAllWallets();
    CreateTransferFromUserResponse createTransferFromUser(CreateTransferFromUserRequest request);
    CreateTransferToUserResponse createTransferToUser(CreateTransferToUserRequest request);
}
