package com.hey.lucky.util;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.payment_service.CreateTransferFromUserRequest;
import com.hey.lucky.dto.payment_service.CreateTransferToUserRequest;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceiveInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;

import java.time.LocalDateTime;
import java.util.List;

public interface LuckyMoneyServiceUtil {
    GetAllWalletsResponse getAllWallets();

    void transferMoneyToUser(CreateTransferToUserRequest request) throws CannotTransferMoneyException;

    boolean isUserInSession(String userId, String sessionId) throws ErrCallApiException;

    List<UserReceiveInfo> getListReceivedUsers(Long luckyMoneyId) throws CannotGetUserInfo;

    UserInfo getUserInfo(String userId) throws CannotGetUserInfo;

    List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user);

    boolean hadUserReceived(long luckyMoneyId, String receiverId);

    void sendMessageReceiveLuckyMoney(String receiverId, String sessionChatId, long luckeyMoneyId, long amount, String wishMessage, LocalDateTime now) throws  ErrCallChatApiException;

    long calculateAmountLuckyMoney(Long restMoney, int restBag, long totalMoney, int totalBag, String type);

    void sendMessageLuckyMoney(String userId, String sessionChatId, String message, long luckyMoneyId, LocalDateTime createdAt) throws ErrCallChatApiException;

    long transferMoneyFromUser(CreateTransferFromUserRequest request) throws CannotTransferMoneyException;

    User getCurrentUser();
}
