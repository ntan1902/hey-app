package com.hey.lucky.util;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceiveInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import com.hey.lucky.exception_handler.exception.UnauthorizeException;

import java.time.LocalDateTime;
import java.util.List;

public interface LuckyMoneyServiceUtil {
    GetAllWalletsResponse getAllWallets();

    void transferMoneyToUser(Long systemWalletId, String receiverId, long amount, String wishMessage) throws CannotTransferMoneyException;

    boolean checkUserInSession(String userId, String sessionId) throws ErrCallApiException, UnauthorizeException;

    List<UserReceiveInfo> getListReceivedUsers(Long luckyMoneyId) throws CannotGetUserInfo;

    UserInfo getUserInfo(String userId) throws CannotGetUserInfo;

    List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user);

    boolean checkOutOfBag(int restBag);

    boolean checkExpiredOfLuckyMoney(LocalDateTime expiredAt, LocalDateTime now);

    boolean checkUserHadReceived(long luckyMoneyId, String receiverId);

    void sendMessageReceiveLuckyMoney(String receiverId, String sessionChatId, long luckeyMoneyId, long amount, String wishMessage, LocalDateTime now);

    long calculateAmountLuckyMoney(Long restMoney, int restBag, long totalMoney, int totalBag, String type);

    void sendMessageLuckyMoney(String userId, String sessionChatId, String message, long luckyMoneyId, LocalDateTime createdAt);

    long transferMoneyFromUser(String userId, Long walletId, String softToken, String message) throws CannotTransferMoneyException;

    User getCurrentUser();
}
