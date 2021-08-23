package com.hey.lucky.util;

import com.hey.lucky.dto.payment_service.TransferFromUserRequest;
import com.hey.lucky.dto.payment_service.TransferToUserRequest;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;

public interface PaymentUtil {
    void transferMoneyToUser(TransferToUserRequest request) throws CannotTransferMoneyException;
    long transferMoneyFromUser(TransferFromUserRequest request) throws CannotTransferMoneyException;

}
