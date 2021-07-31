package com.hey.lucky.util;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceiveInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.User;

import java.time.LocalDateTime;
import java.util.List;

public interface LuckyMoneyServiceUtil {
    GetAllWalletsResponse getAllWallets();

    void transferMoneyToUser(Long systemWalletId, String receiverId, long amount, String wishMessage);

    void checkUserInSession(String userId, String sessionId);

    List<UserReceiveInfo> getListReceivedUsers(Long luckyMoneyId);

    UserInfo getUserInfo(String userId);

    List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user);

    void checkOutOfBag(int restBag);

    void checkExpiredOfLuckyMoney(LocalDateTime expiredAt, LocalDateTime now);

    void checkUserHadReceived(long luckyMoneyId, String receiverId);

    void sendMessageReceiveLuckyMoney(String receiverId, String sessionChatId, long luckeyMoneyId, long amount, String wishMessage, LocalDateTime now);

    long calculateAmountLuckyMoney(Long restMoney, int restBag, long totalMoney, int totalBag, String type);

    void sendMessageLuckyMoney(String userId, String sessionChatId, String message, long luckyMoneyId, LocalDateTime createdAt);

    long transferMoneyFromUser(String userId, Long walletId, String softToken, String message);

    User getCurrentUser();
}
