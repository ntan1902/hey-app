package com.hey.lucky.util;

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
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceiveInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@Log4j2
public class LuckyMoneyServiceUtilImpl implements LuckyMoneyServiceUtil {

    private final ChatApi chatApi;
    private final AuthApi authApi;
    private final PaymentApi paymentApi;
    private final LuckyMoneyMapper luckyMoneyMapper;
    private final ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;


    public LuckyMoneyServiceUtilImpl(ChatApi chatApi, AuthApi authApi, PaymentApi paymentApi, LuckyMoneyMapper luckyMoneyMapper, ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository) {
        this.chatApi = chatApi;
        this.authApi = authApi;
        this.paymentApi = paymentApi;
        this.luckyMoneyMapper = luckyMoneyMapper;
        this.receivedLuckyMoneyRepository = receivedLuckyMoneyRepository;
    }

    @Override
    public GetAllWalletsResponse getAllWallets() {
        return paymentApi.getAllWallets();
    }

    @Override
    public void transferMoneyToUser(CreateTransferToUserRequest request) throws CannotTransferMoneyException {
        log.info("Transfer {} to user {} by wallet {}", request.getAmount(), request.getReceiverId(), request.getWalletId());
        CreateTransferToUserResponse response = paymentApi.createTransferToUser(request);
        if (!response.getSuccess()) {
            throw new CannotTransferMoneyException(response.getMessage());
        }
    }

    @Override
    public boolean isUserInSession(String userId, String sessionId) throws ErrCallApiException {
        log.info("Check user {} is in group chat {}", userId, sessionId);
        CheckUserInSessionChatResponse response = chatApi.checkUserInSessionChat(new CheckUserInSessionChatRequest(userId, sessionId));
        if (!response.isSuccess()) {
            throw new ErrCallApiException("Can not verify your authentication. Try later!");
        }
        return response.getPayload().isExisted();
    }

    @Override
    public List<UserReceiveInfo> getListReceivedUsers(Long luckyMoneyId) throws CannotGetUserInfo {
        List<ReceivedLuckyMoney> receivedLuckyMoneyList = receivedLuckyMoneyRepository.findAllByLuckyMoneyId(luckyMoneyId);
        List<UserReceiveInfo> userReceiveInfoList = new ArrayList<>();
        for (ReceivedLuckyMoney receivedLuckyMoney : receivedLuckyMoneyList) {
            UserInfo userInfo = getUserInfo(receivedLuckyMoney.getReceiverId());
            userReceiveInfoList.add(UserReceiveInfo.builder()
                    .fullName(userInfo.getFullName())
                    .amount(receivedLuckyMoney.getAmount())
                    .receivedAt(receivedLuckyMoney.getCreatedAt().toString())
                    .build());
        }
        return userReceiveInfoList;
    }


    @Override
    public UserInfo getUserInfo(String userId) throws CannotGetUserInfo {
        log.info("Get user info from auth service");
        GetUserInfoResponse apiResponse = authApi.getUserInfo(userId);
        if (!apiResponse.getSuccess()) {
            throw new CannotGetUserInfo();
        }
        return apiResponse.getPayload();
    }

    @Override
    public List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user) {
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


    @Override
    public boolean hadUserReceived(long luckyMoneyId, String receiverId) {
        log.info("Check user had received ?");
        return receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(luckyMoneyId, receiverId);
    }

    @Override
    public void sendMessageReceiveLuckyMoney(String receiverId, String sessionChatId, long luckeyMoneyId, long amount, String wishMessage, LocalDateTime now) throws ErrCallChatApiException {
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


    @Override
    public long calculateAmountLuckyMoney(Long restMoney, int restBag, long totalMoney, int totalBag, String type) {
        log.info("Calculate amount user will receive");
        switch (type) {
            case TypeLuckyMoney.RANDOM: {
                if (restBag == 1) {
                    return restMoney;
                }
                long minPerBag = (long) ((totalMoney * 0.9) / totalBag);
                Random random = new Random();
                long randomMoney = restMoney - (minPerBag * restBag);
                long result = minPerBag + (long) ((random.nextDouble() / restBag + random.nextDouble() / totalBag + (double) (restBag) / totalBag / totalBag) * randomMoney);
                if (result > restMoney) return restMoney;
                return result;
            }
            case TypeLuckyMoney.EQUALLY:
            default: {
                return restMoney / restBag;
            }
        }
    }

    @Override
    public void sendMessageLuckyMoney(String userId, String sessionChatId, String message, long luckyMoneyId, LocalDateTime createdAt) throws ErrCallChatApiException {
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

    @Override
    public long transferMoneyFromUser(CreateTransferFromUserRequest request) throws CannotTransferMoneyException {
        log.info("Transfer from user {} to wallet {} by softToken {}", request.getUserId(), request.getWalletId(), request.getSoftToken());

        CreateTransferFromUserResponse response = paymentApi.createTransferFromUser(request);
        if (!response.getSuccess()) {
            log.error("can't transfer money");
            throw new CannotTransferMoneyException(response.getMessage());
        }
        return response.getPayload().getAmount();
    }

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
