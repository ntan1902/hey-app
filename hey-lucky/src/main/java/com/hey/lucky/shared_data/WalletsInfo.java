package com.hey.lucky.shared_data;

import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.payment_service.WalletSystemDTO;
import com.hey.lucky.exception_handler.exception.CannotGetWalletException;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@Data
@Log4j2
public class WalletsInfo {
    private final AtomicInteger current = new AtomicInteger(0);
    private List<Long> walletIds;
    private int numberWallet;

    private final PaymentApi paymentApi;

    public WalletsInfo(PaymentApi paymentApi) {
        this.paymentApi = paymentApi;
    }


    public long getCurrentWallet(){
        int currentWallet = current.getAndUpdate(c -> c < numberWallet - 1 ? c + 1 : 0);
        return walletIds.get(currentWallet);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void getAllWallets() {
        GetAllWalletsResponse getAllWalletsResponse = paymentApi.getAllWallets();
        if (getAllWalletsResponse.getSuccess()) {
            log.info("Get all wallet");

            walletIds = getAllWalletsResponse.getPayload()
                    .stream()
                    .map(WalletSystemDTO::getWalletId)
                    .collect(Collectors.toList());
            numberWallet = walletIds.size();

            log.info("Number of wallets: {}", numberWallet);
        } else {
            log.error("Can't get wallets, message: {}", getAllWalletsResponse.getMessage());
            throw new CannotGetWalletException();
        }
    }
}
