package com.hey.lucky.api;

import com.hey.lucky.dto.payment_service.*;

public interface PaymentApi {
    GetAllWalletsResponse getAllWallets();
    CreateTransferFromUserResponse createTransferFromUser(TransferFromUserRequest request);
    CreateTransferToUserResponse createTransferToUser(TransferToUserRequest request);
}
