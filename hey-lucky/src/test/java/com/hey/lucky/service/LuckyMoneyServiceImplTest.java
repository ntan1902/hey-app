package com.hey.lucky.service;

import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.dto.payment_service.TransferFromUserRequest;
import com.hey.lucky.dto.payment_service.TransferToUserRequest;
import com.hey.lucky.dto.user.*;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.LuckyMoneyRepository;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import com.hey.lucky.shared_data.WalletsInfo;
import com.hey.lucky.util.ChatUtil;
import com.hey.lucky.util.LuckyMoneyServiceUtil;
import com.hey.lucky.util.PaymentUtil;
import com.hey.lucky.util.UserUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LuckyMoneyServiceImplTest {

    @InjectMocks
    private final LuckyMoneyService luckyMoneyService = new LuckyMoneyServiceImpl();

    @Mock
    private LuckyMoneyRepository luckyMoneyRepository;

    @Mock
    private LuckyMoneyServiceUtil luckyMoneyServiceUtil;

    @Mock
    private WalletsInfo walletsInfo;

    @Mock
    private ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;

    @Mock
    private LuckyMoneyMapper luckyMoneyMapper;

    @Mock
    private PaymentUtil paymentUtil;

    @Mock
    private UserUtil userUtil;

    @Mock
    private ChatUtil chatUtil;

    @Test
    void createLuckyMoney_throw_UnauthorizeException() throws ErrCallApiException {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .sessionChatId("abc")
                .numberBag(10)
                .message("happy new year")
                .softToken("abc-123")
                .type(TypeLuckyMoney.RANDOM)
                .amount(50000L)
                .build();
        User user = new User("abc");
        long walletId = 1L;

        when(walletsInfo.getCurrentWallet()).thenReturn(walletId);
        when(userUtil.getCurrentUser()).thenReturn(user);

        when(userUtil.isUserInSession(anyString(), anyString())).thenReturn(false);


        // when
        // then
        assertThrows(UserNotInSessionChatException.class, ()-> luckyMoneyService.createLuckyMoney(request));
    }

    @Test
    void createLuckyMoney() throws ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UnauthorizeException, UserNotInSessionChatException, MinAmountPerBagException {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .sessionChatId("abc")
                .numberBag(10)
                .message("happy new year")
                .softToken("abc-123")
                .type(TypeLuckyMoney.RANDOM)
                .amount(20000L)
                .build();
        User user = new User("abc");
        long walletId = 1L;
        long amount = 20000L;
        LocalDateTime createdAt = LocalDateTime.now();

        when(walletsInfo.getCurrentWallet()).thenReturn(walletId);
        when(userUtil.getCurrentUser()).thenReturn(user);

        when(userUtil.isUserInSession(anyString(), anyString())).thenReturn(true);

        when(paymentUtil.transferMoneyFromUser(any(TransferFromUserRequest.class))).thenReturn(amount);
        doNothing().when(chatUtil).sendMessageLuckyMoney(any(LuckyMoneyMessageContent.class));
        doAnswer(invocationOnMock -> {
            LuckyMoney firstAgr = invocationOnMock.getArgument(0);
            firstAgr.setId(1L);
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
    void createLuckyMoneyWithMoneyPerBagLessThanMinAmount(){
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .sessionChatId("abc")
                .numberBag(1000)
                .message("happy new year")
                .softToken("abc-123")
                .type(TypeLuckyMoney.RANDOM)
                .amount(20000L)
                .build();
        User user = new User("abc");
        long walletId = 1L;

        when(walletsInfo.getCurrentWallet()).thenReturn(walletId);
        when(userUtil.getCurrentUser()).thenReturn(user);

        // when
        // then
        assertThrows(MinAmountPerBagException.class, ()->luckyMoneyService.createLuckyMoney(request));
    }
    @Test
    void createLuckyMoneyThrowErrCallChatApiException() throws ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UnauthorizeException, UserNotInSessionChatException, MinAmountPerBagException {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .sessionChatId("abc")
                .numberBag(10)
                .message("happy new year")
                .softToken("abc-123")
                .type(TypeLuckyMoney.RANDOM)
                .amount(20000L)
                .build();
        User user = new User("abc");
        long walletId = 1L;
        long amount = 20000L;

        when(walletsInfo.getCurrentWallet()).thenReturn(walletId);
        when(userUtil.getCurrentUser()).thenReturn(user);

        when(userUtil.isUserInSession(anyString(), anyString())).thenReturn(true);

        when(paymentUtil.transferMoneyFromUser(any(TransferFromUserRequest.class))).thenReturn(amount);
        doThrow(new ErrCallChatApiException("Can not send message")).when(chatUtil).sendMessageLuckyMoney(any(LuckyMoneyMessageContent.class));
        doAnswer(invocationOnMock -> {
            LuckyMoney firstAgr = invocationOnMock.getArgument(0);
            firstAgr.setId(1L);
            return null;
        }).when(luckyMoneyRepository).save(any(LuckyMoney.class));
        // when
        // then
        assertThrows(ErrCallChatApiException.class, ()->luckyMoneyService.createLuckyMoney(request));
    }
    @Test
    void createLuckyMoneyThrowException() throws ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UnauthorizeException, UserNotInSessionChatException, MinAmountPerBagException {
        // given
        CreateLuckyMoneyRequest request = CreateLuckyMoneyRequest.builder()
                .sessionChatId("abc")
                .numberBag(10)
                .message("happy new year")
                .softToken("abc-123")
                .type(TypeLuckyMoney.RANDOM)
                .amount(20000L)
                .build();
        User user = new User("abc");
        long walletId = 1L;
        long amount = 20000L;

        LocalDateTime createdAt = LocalDateTime.now();
        LuckyMoney luckyMoney = LuckyMoney.builder()
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

        when(walletsInfo.getCurrentWallet()).thenReturn(walletId);
        when(userUtil.getCurrentUser()).thenReturn(user);

        when(userUtil.isUserInSession(anyString(), anyString())).thenReturn(true);

        when(paymentUtil.transferMoneyFromUser(any(TransferFromUserRequest.class))).thenReturn(amount);

        doThrow(new RuntimeException()).when(luckyMoneyRepository).save(luckyMoney);
        doNothing().when(chatUtil).sendMessageLuckyMoney(any(LuckyMoneyMessageContent.class));

        TransferToUserRequest expected = TransferToUserRequest.builder()
                .amount(amount)
                .receiverId(user.getId())
                .message("Refund")
                .walletId(walletId)
                .build();
        // when
        luckyMoneyService.createLuckyMoney(request);
        // then
        ArgumentCaptor<TransferToUserRequest> argumentCaptor = ArgumentCaptor.forClass(TransferToUserRequest.class);
        verify(paymentUtil, times(1)).transferMoneyToUser(argumentCaptor.capture());
        TransferToUserRequest actual = argumentCaptor.getValue();
        assertThat(expected).isEqualTo(actual);
    }

    @Test
    void receiveLuckyMoney_throw_LuckyMoneyInvalidException() {
        // given
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1L).build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())).thenReturn(Optional.empty());
        // when
//        luckyMoneyService.receiveLuckyMoney(request);
        // then
//        assertThatThrownBy(luckyMoneyService.receiveLuckyMoney(request));
        assertThrows(InvalidLuckyMoneyException.class, () -> luckyMoneyService.receiveLuckyMoney(request));

    }

    @Test
    void receiveLuckyMoney_throw_UnauthorizedException() throws ErrCallApiException {
        // given
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1L).build();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(request.getLuckyMoneyId())
                .userId(user.getId())
                .systemWalletId(10L)
                .sessionChatId("123-abc")
                .amount(50_000L)
                .restMoney(40_000L)
                .numberBag(10)
                .restBag(8)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("Hello 123")
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(false);
        when(luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())).thenReturn(Optional.of(luckyMoney));
        // when
        // then
        assertThrows(UserNotInSessionChatException.class, () -> luckyMoneyService.receiveLuckyMoney(request));

    }

    @Test
    void receiveLuckyMoney_throw_LuckyMoneyExpiredException() throws ErrCallApiException {
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1L).build();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(2);
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(request.getLuckyMoneyId())
                .userId(user.getId())
                .systemWalletId(10L)
                .sessionChatId("123-abc")
                .amount(50_000L)
                .restMoney(40_000L)
                .numberBag(10)
                .restBag(8)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("Hello 123")
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())).thenReturn(Optional.of(luckyMoney));
        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(true);
        // when
//        luckyMoneyService.receiveLuckyMoney(request);
        // then
        assertThrows(LuckyMoneyExpiredException.class, () -> luckyMoneyService.receiveLuckyMoney(request));

    }

    @Test
    void receiveLuckyMoney_throw_HadReceivedException() throws ErrCallApiException {
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1L).build();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(10);
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(request.getLuckyMoneyId())
                .userId(user.getId())
                .systemWalletId(10L)
                .sessionChatId("123-abc")
                .amount(50_000L)
                .restMoney(40_000L)
                .numberBag(10)
                .restBag(8)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("Hello 123")
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())).thenReturn(Optional.of(luckyMoney));
        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(true);
        when(luckyMoneyServiceUtil.hadUserReceived(luckyMoney.getId(), user.getId())).thenReturn(true);

        // when
//        luckyMoneyService.receiveLuckyMoney(request);
        // then
        assertThrows(HadReceivedException.class, () -> luckyMoneyService.receiveLuckyMoney(request));
    }

    @Test
    void receiveLuckyMoney_throw_OutOfBagException() throws ErrCallApiException {
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1L).build();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(2);
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(request.getLuckyMoneyId())
                .userId(user.getId())
                .systemWalletId(10L)
                .sessionChatId("123-abc")
                .amount(50_000L)
                .restMoney(40_000L)
                .numberBag(10)
                .restBag(0)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("Hello 123")
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())).thenReturn(Optional.of(luckyMoney));
        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(true);
        when(luckyMoneyServiceUtil.hadUserReceived(luckyMoney.getId(), user.getId())).thenReturn(false);

        // when
//        luckyMoneyService.receiveLuckyMoney(request);
        // then
        assertThrows(OutOfBagException.class, () -> luckyMoneyService.receiveLuckyMoney(request));

    }

    @Test
    void receiveLuckyMoneySuccessfully() throws InvalidLuckyMoneyException, ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UnauthorizeException, LuckyMoneyExpiredException, OutOfBagException, HadReceivedException, UserNotInSessionChatException {
        // given
        User user = new User("abc");
        ReceiveLuckyMoneyRequest request = ReceiveLuckyMoneyRequest.builder()
                .luckyMoneyId(1L).build();
        LocalDateTime now = LocalDateTime.now();
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(1L)
                .userId(user.getId())
                .systemWalletId(1L)
                .sessionChatId("123abc")
                .amount(50_000L)
                .restMoney(45_000L)
                .numberBag(10)
                .restBag(9)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("haha hihi")
                .createdAt(now)
                .expiredAt(now.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.getLuckyMoneyById(anyLong())).thenReturn(Optional.of(luckyMoney));

        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(true);
        when(luckyMoneyServiceUtil.hadUserReceived(luckyMoney.getId(), user.getId())).thenReturn(false);
        long amount = 3_000L;
        when(luckyMoneyServiceUtil.calculateAmountLuckyMoney(any(LuckyMoney.class))).thenReturn(amount);

        doNothing().when(paymentUtil).transferMoneyToUser(any(TransferToUserRequest.class));
        doNothing().when(chatUtil).sendMessageReceiveLuckyMoney(any(ReceiveLuckyMoneyMessageContent.class));
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
        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("id", "createdAt")
                .isEqualTo(actual);
    }
    @Test
    void getAllLuckyMoneyOfSessionWithNotExistSession() throws ErrCallApiException, UnauthorizeException, UserNotInSessionChatException, CannotGetUserInfo {
        // given
        User user = new User("abc");
        String sessionId = "-1";

        when(userUtil.getCurrentUser()).thenReturn(user);
        List<LuckyMoneyDTO> expected = new ArrayList<>();
        // when
        List<LuckyMoneyDTO> actual = luckyMoneyService.getAllLuckyMoneyOfSession(sessionId);
        // then
        assertThat(expected).isEqualTo(actual);
    }
    @Test
    void getAllLuckyMoneyOfSession_throw_UnauthorizeException() throws ErrCallApiException {
        // given
        User user = new User("abc");
        String sessionId = "abc-123";

        when(userUtil.getCurrentUser()).thenReturn(user);
        when(userUtil.isUserInSession(user.getId(), sessionId)).thenReturn(false);
        // when
        // then
        assertThrows(UserNotInSessionChatException.class, () -> luckyMoneyService.getAllLuckyMoneyOfSession(sessionId));

    }

    @Test
    void getAllLuckyMoneyOfSession_successfully() throws ErrCallApiException, UnauthorizeException, CannotGetUserInfo, UserNotInSessionChatException {
        // given
        User user = new User("abc");
        String sessionId = "123-abc";
        List<LuckyMoney> expected = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        expected.add(LuckyMoney.builder()
                .sessionChatId("123-abc")
                .userId("123")
                .amount(20000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.RANDOM)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now)
                .expiredAt(now.plusDays(1))
                .build());
        expected.add(LuckyMoney.builder()
                .sessionChatId("123-abc")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now)
                .expiredAt(now.plusDays(1))
                .build());
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(userUtil.isUserInSession(user.getId(), sessionId)).thenReturn(true);
        when(luckyMoneyRepository.findAllBySessionChatId(sessionId)).thenReturn(expected);
        // when
        luckyMoneyService.getAllLuckyMoneyOfSession(sessionId);
        // then
        ArgumentCaptor<List<LuckyMoney>> argumentCaptor = ArgumentCaptor.forClass(List.class);
        verify(luckyMoneyServiceUtil, times(1)).luckyMoneyList2LuckyMoneyDTOList(argumentCaptor.capture(), eq(user));
        List<LuckyMoney> actual = argumentCaptor.getValue();
        assertEquals(expected, actual);
    }

    @Test
    void getDetailsLuckyMoney_InvalidLuckyMoneyException() {
        // given
        User user = new User("abc");

        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.findLuckyMoneyById(anyLong())).thenReturn(Optional.empty());
        // when
        // then
        assertThrows(InvalidLuckyMoneyException.class, () -> luckyMoneyService.getLuckyMoneyDetails(123L));
    }

    @Test
    void getDetailsLuckyMoney_throw_UnauthorizeExcepion() throws ErrCallApiException {
        // given
        User user = new User("abc");
        LocalDateTime now = LocalDateTime.now();
        Long luckyMoneyId = 10L;
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(luckyMoneyId)
                .sessionChatId("123-abc")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now)
                .expiredAt(now.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.findLuckyMoneyById(luckyMoneyId)).thenReturn(Optional.of(luckyMoney));
        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(false);
        // when
        // then
        assertThrows(UserNotInSessionChatException.class, () -> luckyMoneyService.getLuckyMoneyDetails(luckyMoneyId));
    }

    @Test
    void getDetailsLuckyMoney_Successfully() throws ErrCallApiException, UnauthorizeException, CannotGetUserInfo, InvalidLuckyMoneyException, UserNotInSessionChatException {
        User user = new User("abc");
        LocalDateTime now = LocalDateTime.now();
        Long luckyMoneyId = 10L;
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .id(luckyMoneyId)
                .sessionChatId("123-abc")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now)
                .expiredAt(now.plusDays(1))
                .build();
        when(userUtil.getCurrentUser()).thenReturn(user);
        when(luckyMoneyRepository.findLuckyMoneyById(luckyMoneyId)).thenReturn(Optional.of(luckyMoney));
        when(userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())).thenReturn(true);


        UserInfo senderLuckyMoney = new UserInfo();
        senderLuckyMoney.setId("123abc");
        senderLuckyMoney.setEmail("ngoctrong102@gmail.com");
        senderLuckyMoney.setFullName("Vo Ngoc Trong");
        senderLuckyMoney.setUsername("ngoctrong102");

        List<UserReceiveInfo> receivedUsers = new ArrayList<>();

        when(userUtil.getUserInfo(luckyMoney.getUserId())).thenReturn(senderLuckyMoney);

        when(luckyMoneyServiceUtil.getListReceivedUsers(luckyMoney.getId())).thenReturn(receivedUsers);


        LuckyMoneyDetails luckyMoneyDetails = LuckyMoneyDetails.builder()
                .totalMoney(50_000L)
                .restMoney(10_000L)
                .totalBag(10)
                .restBag(1)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("hello")
                .build();

        LuckyMoneyDetails expected = LuckyMoneyDetails.builder()
                .userCreated(senderLuckyMoney)
                .usersReceived(receivedUsers)
                .totalMoney(50_000L)
                .restMoney(10_000L)
                .totalBag(10)
                .restBag(1)
                .type(TypeLuckyMoney.EQUALLY)
                .wishMessage("hello")
                .build();

        when(luckyMoneyMapper.luckyMoney2LuckyMoneyDetails(luckyMoney)).thenReturn(luckyMoneyDetails);
        // when
        LuckyMoneyDetails actual = luckyMoneyService.getLuckyMoneyDetails(luckyMoneyId);
        // then
        assertEquals(expected, actual);
    }

    @Test
    void refundLuckyMoney_throw_CannotTranferMoneyException() throws CannotTransferMoneyException {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plusMinutes(2);
        List<LuckyMoney> luckyMoneyList = new ArrayList<>();
        luckyMoneyList.add(LuckyMoney.builder()
                .sessionChatId("123-abc")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now.minusDays(1))
                .expiredAt(expired)
                .build());
        luckyMoneyList.add(LuckyMoney.builder()
                .sessionChatId("123-456")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now.minusDays(1))
                .expiredAt(expired)
                .build());
        when(luckyMoneyRepository.getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween(any(LocalDateTime.class),any(LocalDateTime.class))).thenReturn(luckyMoneyList);
        doThrow(new CannotTransferMoneyException("Error from payment service")).when(paymentUtil).transferMoneyToUser(any(TransferToUserRequest.class));
        // when
        luckyMoneyService.refundLuckyMoney();
        // then
        ArgumentCaptor<LuckyMoney> argSave = ArgumentCaptor.forClass(LuckyMoney.class);
        verify(luckyMoneyRepository,times(4)).save(argSave.capture());
        List<LuckyMoney> actuals = argSave.getAllValues();

        assertTrue(actuals.get(2).getExpiredAt().isAfter(expired));
        assertTrue(actuals.get(3).getExpiredAt().isAfter(expired));
    }

    @Test
    void refundLuckyMoney_Successfully() throws CannotTransferMoneyException {
        // given
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plusMinutes(2);
        List<LuckyMoney> luckyMoneyList = new ArrayList<>();
        luckyMoneyList.add(LuckyMoney.builder()
                .sessionChatId("123-abc")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now.minusDays(1))
                .expiredAt(expired)
                .build());
        luckyMoneyList.add(LuckyMoney.builder()
                .sessionChatId("123-456")
                .userId("123")
                .amount(50000L)
                .wishMessage("hello")
                .type(TypeLuckyMoney.EQUALLY)
                .restMoney(10000L)
                .numberBag(10)
                .restBag(1)
                .systemWalletId(12L)
                .createdAt(now.minusDays(1))
                .expiredAt(expired)
                .build());
        when(luckyMoneyRepository.getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween(any(LocalDateTime.class),any(LocalDateTime.class))).thenReturn(luckyMoneyList);
        doNothing().when(paymentUtil).transferMoneyToUser(any(TransferToUserRequest.class));
        // when
        luckyMoneyService.refundLuckyMoney();
        // then
        ArgumentCaptor<LuckyMoney> argSave = ArgumentCaptor.forClass(LuckyMoney.class);
        verify(luckyMoneyRepository,times(2)).save(argSave.capture());
        List<LuckyMoney> actuals = argSave.getAllValues();

        for (LuckyMoney luckyMoney : actuals){
            assertEquals( expired,luckyMoney.getExpiredAt());
            assertEquals(0L,luckyMoney.getRestMoney());
        }
    }

}