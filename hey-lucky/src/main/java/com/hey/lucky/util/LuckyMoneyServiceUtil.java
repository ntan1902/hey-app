package com.hey.lucky.util;

import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceivedInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;

import java.util.List;

public interface LuckyMoneyServiceUtil {

    List<UserReceivedInfo> getListReceivedUsers(Long luckyMoneyId) throws CannotGetUserInfo;

    List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user) throws CannotGetUserInfo;

    boolean hadUserReceived(long luckyMoneyId, String receiverId);

    long calculateAmountLuckyMoney(LuckyMoney luckyMoney);

}
