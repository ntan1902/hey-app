package com.hey.lucky.shared_data;

import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.payment_service.WalletSystemDTO;
import com.hey.lucky.exception_handler.exception.CannotGetWalletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletsInfoTest {
    @InjectMocks
    private WalletsInfo walletsInfo;

    @Mock
    private PaymentApi paymentApi;

    @Test
    void getAllWalletsSuccessfully() {
        // given
        List<WalletSystemDTO> walletSystemDTOList = new ArrayList<>();
        walletSystemDTOList.add(WalletSystemDTO.builder()
                .walletId(1)
                .balance(0)
                .build());
        walletSystemDTOList.add(WalletSystemDTO.builder()
                .walletId(2)
                .balance(0)
                .build());
        walletSystemDTOList.add(WalletSystemDTO.builder()
                .walletId(3)
                .balance(0)
                .build());
        GetAllWalletsResponse expected = GetAllWalletsResponse.builder()
                .success(true)
                .code(200)
                .message("Success")
                .payload(walletSystemDTOList)
                .build();
        when(paymentApi.getAllWallets())
                .thenReturn(
                        expected
                );
        // when
        // then
        assertThatNoException().isThrownBy(() -> walletsInfo.getAllWallets());
    }


    @Test
    void getAllWalletsFailed() {
        // given
        when(paymentApi.getAllWallets()).thenReturn(GetAllWalletsResponse.builder()
                .success(false)
                .code(400)
                .message("")
                .payload(new ArrayList<>())
                .build());
        // when
        // then
        assertThrows(CannotGetWalletException.class, () -> walletsInfo.getAllWallets());
    }
}