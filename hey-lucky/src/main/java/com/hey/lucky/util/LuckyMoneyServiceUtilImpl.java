package com.hey.lucky.util;

import com.hey.lucky.constant.TypeLuckyMoney;
import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.user.LuckyMoneyDTO;
import com.hey.lucky.dto.user.UserReceivedInfo;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.CannotGetUserInfo;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Log4j2
public class LuckyMoneyServiceUtilImpl implements LuckyMoneyServiceUtil {

    public static final int MIN_AMOUNT = 100;
    private final LuckyMoneyMapper luckyMoneyMapper;
    private final ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;
    private final UserUtil userUtil;


    public LuckyMoneyServiceUtilImpl( LuckyMoneyMapper luckyMoneyMapper, ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository, UserUtil userUtil) {
       this.luckyMoneyMapper = luckyMoneyMapper;
        this.receivedLuckyMoneyRepository = receivedLuckyMoneyRepository;
        this.userUtil = userUtil;
    }



    @Override
    public List<UserReceivedInfo> getListReceivedUsers(Long luckyMoneyId) throws CannotGetUserInfo {
        List<ReceivedLuckyMoney> receivedLuckyMoneyList = receivedLuckyMoneyRepository.findAllByLuckyMoneyId(luckyMoneyId);
        List<UserReceivedInfo> userReceivedInfoList = new ArrayList<>();
        for (ReceivedLuckyMoney receivedLuckyMoney : receivedLuckyMoneyList) {
            UserInfo userInfo = userUtil.getUserInfo(receivedLuckyMoney.getReceiverId());
            userReceivedInfoList.add(UserReceivedInfo.builder()
                    .fullName(userInfo.getFullName())
                    .amount(receivedLuckyMoney.getAmount())
                    .receivedAt(receivedLuckyMoney.getCreatedAt().toString())
                    .build());
        }
        return userReceivedInfoList;
    }


    @Override
    public List<LuckyMoneyDTO> luckyMoneyList2LuckyMoneyDTOList(List<LuckyMoney> luckyMoneyList, User user) throws CannotGetUserInfo {
        List<LuckyMoneyDTO> luckyMoneyDTOList = new ArrayList<>();
        for (LuckyMoney luckyMoney : luckyMoneyList) {
            UserInfo sender = userUtil.getUserInfo(luckyMoney.getUserId());
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
            luckyMoneyDTOList.add(luckyMoneyDTO);
        }
        return luckyMoneyDTOList;
    }


    @Override
    public boolean hadUserReceived(long luckyMoneyId, String receiverId) {
        log.info("Check user had received ?");
        return receivedLuckyMoneyRepository.existsByLuckyMoneyIdAndReceiverId(luckyMoneyId, receiverId);
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
                if (minPerBag < MIN_AMOUNT){
                    return MIN_AMOUNT;
                }
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


}
