package com.hey.lucky.util;

import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.dto.payment_service.CreateTransferFromUserResponse;
import com.hey.lucky.dto.payment_service.CreateTransferToUserResponse;
import com.hey.lucky.dto.payment_service.TransferFromUserRequest;
import com.hey.lucky.dto.payment_service.TransferToUserRequest;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class PaymentUtilImpl implements PaymentUtil{

    private final PaymentApi paymentApi;

    public PaymentUtilImpl(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }

    @Override
    public void transferMoneyToUser(TransferToUserRequest request) throws CannotTransferMoneyException {
        log.info("Transfer {} to user {} by wallet {}", request.getAmount(), request.getReceiverId(), request.getWalletId());
        CreateTransferToUserResponse response = paymentApi.createTransferToUser(request);
        if (!response.getSuccess()) {
            throw new CannotTransferMoneyException(response.getMessage());
        }
    }

    @Override
    public long transferMoneyFromUser(TransferFromUserRequest request) throws CannotTransferMoneyException {
        log.info("Transfer from user {} to wallet {} by softToken {}", request.getUserId(), request.getWalletId(), request.getSoftToken());

        CreateTransferFromUserResponse response = paymentApi.createTransferFromUser(request);
        if (!response.getSuccess()) {
            log.error("can't transfer money");
            throw new CannotTransferMoneyException(response.getMessage());
        }
        return response.getPayload().getAmount();
    }
}
