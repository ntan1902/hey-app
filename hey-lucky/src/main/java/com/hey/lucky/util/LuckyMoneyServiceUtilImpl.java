package com.hey.lucky.util;

import com.hey.lucky.api.AuthApi;
import com.hey.lucky.api.ChatApi;
import com.hey.lucky.api.PaymentApi;
import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.auth_service.GetUserInfoResponse;
import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatRequest;
import com.hey.lucky.dto.chat_service.CheckUserInSessionChatResponse;
import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.dto.payment_service.*;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceiveInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import com.hey.lucky.exception_handler.exception.ErrCallChatApiException;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public void transferMoneyToUser(TransferToUserRequest request) throws CannotTransferMoneyException {
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
    public List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user) throws CannotGetUserInfo {
        List<LuckyMoneyDTO> luckyMoneyDTOList = new ArrayList<>();
        for (LuckyMoney luckyMoney : luckyMoneyList){
            UserInfo sender = getUserInfo(luckyMoney.getUserId());
            LuckyMoneyDTO luckyMoneyDTO = luckyMoneyMapper.luckyMoney2LuckyMoneyDTO(luckyMoney);
            luckyMoneyDTO.setSenderName(sender.getFullName());
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
            luckyMoneyDTOList.add(  luckyMoneyDTO);
        }
        return luckyMoneyDTOList;
    }


    @Override
    public boolean hadUserReceived(long luckyMoneyId, String receiverId) {
        log.info("Check user had received ?");
        return receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(luckyMoneyId, receiverId);
    }

    @Override
    public void sendMessageReceiveLuckyMoney(ReceiveLuckyMoneyMessageContent request) throws ErrCallChatApiException {
        log.info("Send message receive lucky money");
        chatApi.createReceiveLuckyMoneyMessage(request);
    }


    @Override
    public long calculateAmountLuckyMoney(LuckyMoney luckyMoney) {
        log.info("Calculate amount user will receive");
        switch (luckyMoney.getType()) {
            case TypeLuckyMoney.RANDOM: {
                if (luckyMoney.getRestBag() == 1) {
                    return luckyMoney.getRestMoney();
                }
                long minPerBag = (long) ((luckyMoney.getAmount() * 0.9) / luckyMoney.getNumberBag());
                Random random = new Random();
                long randomMoney = luckyMoney.getRestMoney() - (minPerBag * luckyMoney.getRestBag());
                long result = minPerBag + (long) ((random.nextDouble() / luckyMoney.getRestBag() + random.nextDouble() / luckyMoney.getNumberBag() + (double) (luckyMoney.getRestBag()) / luckyMoney.getNumberBag() / luckyMoney.getNumberBag()) * randomMoney);
                if (result > luckyMoney.getRestMoney()) return luckyMoney.getRestMoney();
                return result;
            }
            case TypeLuckyMoney.EQUALLY:
            default: {
                return luckyMoney.getRestMoney() / luckyMoney.getRestBag();
            }
        }
    }

    @Override
    public void sendMessageLuckyMoney(LuckyMoneyMessageContent request) throws ErrCallChatApiException {
        log.info("Send message lucky money");
        chatApi.createLuckyMoneyMessage(request);
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

    @Override
    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
