package com.hey.lucky.service;

import com.hey.lucky.api.AuthApi;
import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.dto.user.ReceiveLuckyMoneyRequest;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.LuckyMoneyInvalidException;
import com.hey.lucky.repository.LuckyMoneyRepository;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import com.hey.lucky.shared_data.WalletsInfo;
import com.hey.lucky.util.LuckyMoneyServiceUtil;
import com.hey.lucky.util.LuckyMoneyServiceUtilImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.extractProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LuckyMoneyServiceImplTest {

    @InjectMocks
    private LuckyMoneyServiceImpl luckyMoneyService;

    @Mock
    private AuthApi authApi;

    @Mock
    private PaymentApi paymentApi;

    @Mock
    private LuckyMoneyRepository luckyMoneyRepository;

    @Mock
    private LuckyMoneyServiceUtil luckyMoneyServiceUtil;

    @Mock
    private WalletsInfo walletsInfo;

    @Mock
    private ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;

    @Test
    void createLuckyMoney() {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .sessionChatId("abc")
                .numberBag(10)
                .message("happy new year")
                .softToken("abc-123")
                .type(TypeLuckyMoney.RANDOM)
                .build();
        User user = new User("abc");
        long walletId = 1l;
        long amount = 20000l;
        LocalDateTime createdAt = LocalDateTime.now();

        when(walletsInfo.getCurrentWallet()).thenReturn(walletId);
        when(luckyMoneyServiceUtil.getCurrentUser()).thenReturn(user);

        doNothing().when(luckyMoneyServiceUtil).checkUserInSession(anyString(), anyString());

        when(luckyMoneyServiceUtil.transferMoneyFromUser(anyString(), anyLong(), anyString(), anyString())).thenReturn(amount);
        doNothing().when(luckyMoneyServiceUtil).sendMessageLuckyMoney(anyString(), anyString(), anyString(), anyLong(), any());
        doAnswer(invocationOnMock -> {
            LuckyMoney firstAgr = invocationOnMock.getArgument(0);
            firstAgr.setId(1l);
            return null;
        }).when(luckyMoneyRepository).save(any(LuckyMoney.class));
        LuckyMoney expected = LuckyMoney.builder()
                .userId(user.getId())
                .systemWalletId(walletId)
                .sessionChatId(request.getSessionChatId())
                .amount(amount)
                .restMoney(amount)
                .numberBag(request.getNumberBag())
                .restBag(request.getNumberBag())
                .type(request.getType())
                .wishMessage(request.getMessage())
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        // when
        luckyMoneyService.createLuckyMoney(request);
        // then
        ArgumentCaptor<LuckyMoney> luckyMoneyCaptor = ArgumentCaptor.forClass(LuckyMoney.class);
        verify(luckyMoneyRepository, times(1)).save(luckyMoneyCaptor.capture());
        LuckyMoney actual = luckyMoneyCaptor.getValue();
        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt", "expiredAt")
                .isEqualTo(actual);
    }

    @Test
    void receiveLuckyMoney_throw_LuckyMoneyInvalidException() {
        // given
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1l).build();
        when(luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())).thenReturn(Optional.of(null));
        // when
//        luckyMoneyService.receiveLuckyMoney(request);
        // then
//        assertThatThrownBy(luckyMoneyService.receiveLuckyMoney(request));
        assertThrows(LuckyMoneyInvalidException.class,() -> luckyMoneyService.receiveLuckyMoney(request));

    }

    @Test
    void receiveLuckyMoneySuccessfully() {
        // given
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1l).build();
        LocalDateTime now = LocalDateTime.now();
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(1l)
                .userId(user.getId())
                .systemWalletId(1l)
                .sessionChatId("123abc")
                .amount(50_000l)
                .restMoney(45_000l)
                .numberBag(10)
                .restBag(9)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("haha hihi")
                .createdAt(now)
                .expiredAt(now.plusDays(1))
                .build();

        when(luckyMoneyRepository.getLuckyMoneyById(anyLong())).thenReturn(Optional.of(luckyMoney));
        doNothing().when(luckyMoneyServiceUtil).checkUserInSession(anyString(),eq(luckyMoney.getSessionChatId()));
        doNothing().when(luckyMoneyServiceUtil).checkExpiredOfLuckyMoney(eq(luckyMoney.getExpiredAt()),any());
        doNothing().when(luckyMoneyServiceUtil).checkOutOfBag(luckyMoney.getRestBag());
        doNothing().when(luckyMoneyServiceUtil).checkUserHadReceived(luckyMoney.getId(),user.getId());
        long amount = 3_000l;
        when(luckyMoneyServiceUtil.calculateAmountLuckyMoney(anyLong(),anyInt(),anyLong(),anyInt(),anyString())).thenReturn(amount);
        doNothing()
                .when(luckyMoneyServiceUtil)
                .transferMoneyToUser(anyLong(), anyString(), any(), anyString());
        doNothing().when(luckyMoneyServiceUtil).sendMessageReceiveLuckyMoney(anyString(),anyString(),anyInt(),anyInt(),anyString(),any());
        ReceivedLuckyMoney expected = ReceivedLuckyMoney.builder()
                .luckyMoneyId(luckyMoney.getId())
                .receiverId(user.getId())
                .amount(amount)
                .createdAt(now).build();

        // when
        luckyMoneyService.receiveLuckyMoney(request);
        // then
        ArgumentCaptor<ReceivedLuckyMoney> receivedLuckyMoneyArgumentCaptor = ArgumentCaptor.forClass(ReceivedLuckyMoney.class);
        verify(receivedLuckyMoneyRepository).save(receivedLuckyMoneyArgumentCaptor.capture());
        ReceivedLuckyMoney actual = receivedLuckyMoneyArgumentCaptor.getValue();
        assertEquals(expected,actual);
    }

    @Test
    void getAllLuckyMoney() {
    }

    @Test
    void getDetailsLuckyMoney() {
    }

    @Test
    void refundLuckyMoney() {
    }

    @Test
    void sendMessageLuckyMoney() {
    }
}