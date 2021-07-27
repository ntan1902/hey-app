package com.hey.lucky.service;

import com.hey.lucky.api.AuthApi;
import com.hey.lucky.api.ChatApi;
import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.auth_service.GetUserInfoResponse;
import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatRequest;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatResponse;
import com.hey.lucky.dto.chat_service.CreateLuckyMoneyMessageRequest;
import com.hey.lucky.dto.chat_service.CreateReceiveLuckyMoneyMessageRequest;
import com.hey.lucky.dto.payment_service.*;
import com.hey.lucky.dto.user.*;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.LuckyMoneyRepository;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Log4j2
public class LuckyMoneyServiceImpl implements LuckyMoneyService {
    private static AtomicInteger count = new AtomicInteger(0);
    private static List<Long> walletIds = new ArrayList<>();
    private static int numberWallet;

    private final LuckyMoneyRepository luckyMoneyRepository;
    private final ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;
    private final PaymentApi paymentApi;
    private final ChatApi chatApi;
    private final LuckyMoneyMapper luckyMoneyMapper;
    private final AuthApi authApi;

    public LuckyMoneyServiceImpl(LuckyMoneyRepository luckyMoneyRepository, ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository, PaymentApi paymentApi, ChatApi chatApi, LuckyMoneyMapper luckyMoneyMapper, AuthApi authApi) {
        this.luckyMoneyRepository = luckyMoneyRepository;
        this.receivedLuckyMoneyRepository = receivedLuckyMoneyRepository;
        this.paymentApi = paymentApi;
        this.chatApi = chatApi;
        this.luckyMoneyMapper = luckyMoneyMapper;
        this.authApi = authApi;
    }


    @PostConstruct
    private void getAllWallets() {
        GetAllWalletsResponse getAllWalletsResponse = paymentApi.getAllWallets();
        if (getAllWalletsResponse.getSuccess()) {
            log.info("Get all wallet");
            walletIds = getAllWalletsResponse.getPayload().stream().map(walletSystemDTO -> walletSystemDTO.getWalletId()).collect(Collectors.toList());
            numberWallet = walletIds.size();
            log.info("Number of wallets: {}", numberWallet);
        } else {
            log.error("Can't get wallets, message: {}", getAllWalletsResponse.getMessage());
            System.exit(1);
        }
    }

    @Override
    @Transactional(noRollbackFor = ErrCallApiException.class)
    public void createLuckyMoney(CreateLuckyMoneyRequest request) {
        int currentWallet = count.getAndUpdate(c -> c < numberWallet - 1 ? c + 1 : 0);
        User user = getCurrentUser();
        long userId = user.getId();
        long walletId = walletIds.get(currentWallet);
        String sessionChatId = request.getSessionChatId();
        log.info("Send lucky money for user {} by wallet {}", user.getId(), walletId);

        checkUserInSession(user.getId(), request.getSessionChatId());

        long amount = transferMoneyFromUser(userId, walletId, request.getSoftToken(), request.getMessage());

        log.info("Amount: {}", amount);
        LocalDateTime createdAt = LocalDateTime.now();
        LuckyMoney luckyMoney = LuckyMoney.builder()
                .userId(userId)
                .systemWalletId(walletId)
                .sessionChatId(sessionChatId)
                .amount(amount)
                .restMoney(amount)
                .numberBag(request.getNumberBag())
                .restBag(request.getNumberBag())
                .type(request.getType())
                .wishMessage(request.getMessage())
                .createdAt(createdAt)
                .expiredAt(createdAt.plusDays(1))
                .build();
        luckyMoneyRepository.save(luckyMoney);
        log.info("Send message lucky money");
        sendMessageLuckyMoney(userId, sessionChatId, request.getMessage(), luckyMoney.getId(), createdAt);
        log.info("Send message lucky money success");
    }

    @Override
    @Transactional(noRollbackFor = ErrCallApiException.class)
    public void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) {
        User user = getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LuckyMoney luckyMoney = luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())
                .orElseThrow(() -> {
                    throw new LuckyMoneyInvalidException();
                });
        log.info("User {} receive lucky money {}", user.getId(), luckyMoney.getId());
        checkUserInSession(user.getId(), luckyMoney.getSessionChatId());

        checkExpiredOfLuckyMoney(luckyMoney.getExpiredAt(), now);
        checkOutOfBag(luckyMoney.getRestBag());
        checkUserHadReceived(luckyMoney.getId(), user.getId());


        long restMoney = luckyMoney.getRestMoney();
        int restBag = luckyMoney.getRestBag();
        long amount = calculateAmountLuckyMoney(restMoney, restBag, luckyMoney.getAmount(), luckyMoney.getNumberBag(), luckyMoney.getType());

        transferMoneyToUser(luckyMoney.getSystemWalletId(), user.getId(), amount, String.format("User %d receive %d from lucky money %d", user.getId(), amount, luckyMoney.getId()));

        luckyMoney.setRestMoney(restMoney - amount);
        luckyMoney.setRestBag(restBag - 1);

        luckyMoneyRepository.save(luckyMoney);

        ReceivedLuckyMoney receivedLuckyMoney = ReceivedLuckyMoney.builder()
                .luckyMoneyId(luckyMoney.getId())
                .receiverId(user.getId())
                .amount(amount)
                .createdAt(now).build();

        receivedLuckyMoneyRepository.save(receivedLuckyMoney);
        sendMessageReceiveLuckyMoney(user.getId(), luckyMoney.getSessionChatId(), luckyMoney.getId(), amount, luckyMoney.getWishMessage(), now);

    }

    @Override
    public List<LuckyMoneyDTO> getAllLuckyMoney(String sessionId) {
        User user = getCurrentUser();
        log.info("User {} get all lucky money of chat group {}", user.getId(), sessionId);

        checkUserInSession(user.getId(), sessionId);

        List<LuckyMoney> luckyMoneyList = luckyMoneyRepository.findAllBySessionChatId(sessionId);

        return luckyMoneyList2LuckyMoneyDTOList(luckyMoneyList, user);

    }

    @Override
    public LuckyMoneyDetails getDetailsLuckyMoney(long luckyMoneyId) {
        log.info("Get details of lucky money {}", luckyMoneyId);
        User user = getCurrentUser();
        LuckyMoney luckyMoney = luckyMoneyRepository.findLuckyMoneyById(luckyMoneyId)
                .orElseThrow(() -> {
                    log.error("Lucky money {} is not exists", luckyMoneyId);
                    throw new LuckyMoneyInvalidException();
                });
        checkUserInSession(user.getId(), luckyMoney.getSessionChatId());

        LuckyMoneyDetails luckyMoneyDetails = luckyMoneyMapper.luckyMoney2LuckyMoneyDetails(luckyMoney);

        UserInfo userInfo = getUserInfo(luckyMoney.getUserId());

        List<UserReceiveInfo> listReceivedUsers = getListReceivedUsers(luckyMoney.getId());

        luckyMoneyDetails.setUserCreated(userInfo);
        luckyMoneyDetails.setUsersReceived(listReceivedUsers);

        return luckyMoneyDetails;

    }

    @Override
    @Transactional
    public void refundLuckyMoney() {
        LocalDateTime now = LocalDateTime.now();
        List<LuckyMoney> luckyMoneyList = luckyMoneyRepository.getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween(now, now.plusMinutes(6));
        luckyMoneyList.forEach(luckyMoney -> {
            log.info("Return lucky money {} with rest {} for user {}", luckyMoney.getId(), luckyMoney.getRestMoney(), luckyMoney.getUserId());
            try {
                transferMoneyToUser(luckyMoney.getSystemWalletId(), luckyMoney.getUserId(), luckyMoney.getRestMoney(), String.format("Refund %d from lucky money %d for user %d", luckyMoney.getRestMoney(), luckyMoney.getId(), luckyMoney.getUserId()));
                luckyMoney.setRestMoney(0L);
                luckyMoneyRepository.save(luckyMoney);
            } catch (CannotTransferMoneyException exception) {
                log.error("Can't transfer money");
                luckyMoney.setExpiredAt(now.plusMinutes(10));
                luckyMoneyRepository.save(luckyMoney);
            }
        });
    }

    private void transferMoneyToUser(Long systemWalletId, long receiverId, long amount, String wishMessage) {
        log.info("Transfer {} to user {} by wallet {}", amount, receiverId, systemWalletId);
        CreateTransferToUserRequest request = CreateTransferToUserRequest.builder()
                .walletId(systemWalletId)
                .receiverId(receiverId)
                .amount(amount)
                .message(wishMessage)
                .build();
        CreateTransferToUserResponse response = paymentApi.createTransferToUser(request);
        if (!response.getSuccess()) {
            throw new CannotTransferMoneyException(response.getMessage());
        }
    }

    private void checkUserInSession(long userId, String sessionId) {
        log.info("Check user {} is in group chat {}", userId, sessionId);
        CheckUserInSessionChatResponse response = chatApi.checkUserInSessionChat(new CheckUserInSessionChatRequest(userId, sessionId));
        if (!response.isSuccess()) {
            throw new ErrCallApiException("Can not verify your authentication. Try later!");
        }
        if (!response.getPayload().isExisted()) {
            throw new UnauthorizeException("You aren't in that group chat");
        }
    }

    private List<UserReceiveInfo> getListReceivedUsers(Long luckyMoneyId) {
        List<ReceivedLuckyMoney> receivedLuckyMoneyList = receivedLuckyMoneyRepository.findAllByLuckyMoneyId(luckyMoneyId);
        return receivedLuckyMoneyList.stream().map(receivedLuckyMoney -> {
            UserInfo userInfo = getUserInfo(receivedLuckyMoney.getReceiverId());
            return UserReceiveInfo.builder()
                    .fullName(userInfo.getFullName())
                    .amount(receivedLuckyMoney.getAmount())
                    .receivedAt(receivedLuckyMoney.getCreatedAt().toString())
                    .build();
        }).collect(Collectors.toList());
    }


    private UserInfo getUserInfo(long userId) {
        log.info("Get user info from auth service");
        GetUserInfoResponse apiResponse = authApi.getUserInfo(userId);
        if (!apiResponse.getSuccess()) {
            throw new CannotGetUserInfo();
        }
        return apiResponse.getPayload();
    }

    private List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user) {
        return luckyMoneyList.stream().map(luckyMoney -> {
            LuckyMoneyDTO luckyMoneyDTO = luckyMoneyMapper.luckyMoney2LuckyMoneyDTO(luckyMoney);
            ReceivedLuckyMoney receivedLuckyMoney = receivedLuckyMoneyRepository.findByLuckyMoneyIdAndReceiverId(luckyMoney.getId(), user.getId());
            if (receivedLuckyMoney == null) {
                luckyMoneyDTO.setReceived(false);
                luckyMoneyDTO.setReceivedMoney(0L);
                luckyMoneyDTO.setReceivedAt("");
            } else {
                luckyMoneyDTO.setReceived(true);
                luckyMoneyDTO.setReceivedMoney(receivedLuckyMoney.getAmount());
                luckyMoneyDTO.setReceivedAt(receivedLuckyMoney.getCreatedAt().toString());
            }
            return luckyMoneyDTO;
        }).collect(Collectors.toList());
    }


    private void checkOutOfBag(int restBag) {
        if (restBag == 0) {
            throw new OutOfBagException();
        }
    }

    private void checkExpiredOfLuckyMoney(LocalDateTime expiredAt, LocalDateTime now) {
        log.info("Check expired of lucky money");
        if (expiredAt.isBefore(now)) {
            throw new LuckyMoneyExpiredException();
        }
        log.info("Check expired of lucky money successfully");
    }

    private void checkUserHadReceived(long luckyMoneyId, Long receiverId) {
        log.info("Check user had received ?");
        if (receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(luckyMoneyId, receiverId)) {
            throw new HadReceivedException();
        }
        log.info("Not yet");
    }

    private void sendMessageReceiveLuckyMoney(long receiverId, String sessionChatId, long luckeyMoneyId, long amount, String wishMessage, LocalDateTime now) {
        log.info("Send message receive lucky money");
        CreateReceiveLuckyMoneyMessageRequest request = CreateReceiveLuckyMoneyMessageRequest.builder()
                .receiverId(receiverId)
                .sessionId(sessionChatId)
                .luckyMoneyId(luckeyMoneyId)
                .amount(amount)
                .message(wishMessage)
                .createdAt(now.toString()).build();
        chatApi.createReceiveLuckyMoneyMessage(request);
    }


    private long calculateAmountLuckyMoney(Long restMoney, int restBag, long totalMoney, int totalBag, String type) {
        log.info("Calculate amount user will receive");
        switch (type) {
            case TypeLuckyMoney.RANDOM: {
                if (restBag == 1) {
                    return restMoney;
                }
                long minPerBag = (long) ((totalMoney * 0.9) / totalBag);
                Random random = new Random();
                long randomMoney = restMoney - (minPerBag * restBag);
                long result = minPerBag + (long) ((random.nextDouble() / restBag + random.nextDouble()/totalBag + (double) (restBag)/totalBag/totalBag) * randomMoney);
                if (result > restMoney) return restMoney;
                return result;
            }
            case TypeLuckyMoney.EQUALLY:
            default: {
                return restMoney / restBag;
            }
        }
    }

    public void sendMessageLuckyMoney(long userId, String sessionChatId, String message, long luckyMoneyId, LocalDateTime createdAt) {
        log.info("Send message lucky money");
        CreateLuckyMoneyMessageRequest request = CreateLuckyMoneyMessageRequest.builder()
                .userId(userId)
                .sessionId(sessionChatId)
                .message(message)
                .luckyMoneyId(luckyMoneyId)
                .createdAt(createdAt.toString())
                .build();
        chatApi.createLuckyMoneyMessage(request);
    }

    private long transferMoneyFromUser(Long userId, Long walletId, String softToken, String message) {
        log.info("Transfer from user {} to wallet {} by softToken {}", userId, walletId, softToken);
        CreateTransferFromUserRequest request = CreateTransferFromUserRequest.builder()
                .userId(userId)
                .message(message)
                .softToken(softToken)
                .walletId(walletId)
                .build();
        CreateTransferFromUserResponse response = paymentApi.createTransferFromUser(request);
        if (!response.getSuccess()) {
            log.error("can't transfer money");
            throw new CannotTransferMoneyException(response.getMessage());
        }
        return response.getPayload().getAmount();
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
