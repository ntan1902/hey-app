package com.hey.lucky.util;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.dto.payment_service.GetAllWalletsResponse;
import com.hey.lucky.dto.payment_service.TransferFromUserRequest;
import com.hey.lucky.dto.payment_service.TransferToUserRequest;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceiveInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;
import com.hey.lucky.exception_handler.exception.CannotTransferMoneyException;
import com.hey.lucky.exception_handler.exception.ErrCallApiException;
import com.hey.lucky.exception_handler.exception.ErrCallChatApiException;

import java.util.List;

public interface LuckyMoneyServiceUtil {
    GetAllWalletsResponse getAllWallets();

    void transferMoneyToUser(TransferToUserRequest request) throws CannotTransferMoneyException;

    boolean isUserInSession(String userId, String sessionId) throws ErrCallApiException;

    List<UserReceiveInfo> getListReceivedUsers(Long luckyMoneyId) throws CannotGetUserInfo;

    UserInfo getUserInfo(String userId) throws CannotGetUserInfo;

    List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user) throws CannotGetUserInfo;

    boolean hadUserReceived(long luckyMoneyId, String receiverId);

    void sendMessageReceiveLuckyMoney(ReceiveLuckyMoneyMessageContent request) throws  ErrCallChatApiException;

    long calculateAmountLuckyMoney(LuckyMoney luckyMoney);

    void sendMessageLuckyMoney(LuckyMoneyMessageContent request) throws ErrCallChatApiException;

    long transferMoneyFromUser(TransferFromUserRequest request) throws CannotTransferMoneyException;

    User getCurrentUser();
}
