package com.hey.lucky.service;

import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.user.*;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.LuckyMoneyRepository;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import com.hey.lucky.shared_data.WalletsInfo;
import com.hey.lucky.util.LuckyMoneyServiceUtil;
import com.hey.lucky.util.LuckyMoneyServiceUtilImpl;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@NoArgsConstructor
public class LuckyMoneyServiceImpl implements LuckyMoneyService {
    private LuckyMoneyRepository luckyMoneyRepository;
    private ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;
    private LuckyMoneyMapper luckyMoneyMapper;
    private LuckyMoneyServiceUtil luckyMoneyServiceUtil;
    private WalletsInfo walletsInfo;


    public LuckyMoneyServiceImpl(LuckyMoneyRepository luckyMoneyRepository,
                                 ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository,
                                 LuckyMoneyMapper luckyMoneyMapper, LuckyMoneyServiceUtilImpl luckyMoneyServiceUtil, WalletsInfo walletsInfo) {
        this.luckyMoneyRepository = luckyMoneyRepository;
        this.receivedLuckyMoneyRepository = receivedLuckyMoneyRepository;
        this.luckyMoneyMapper = luckyMoneyMapper;
        this.luckyMoneyServiceUtil = luckyMoneyServiceUtil;
        this.walletsInfo = walletsInfo;
    }

    @Override
    @Transactional(noRollbackFor = ErrCallApiException.class)
    public void createLuckyMoney(CreateLuckyMoneyRequest request) {
        long walletId = walletsInfo.getCurrentWallet();
        User user = luckyMoneyServiceUtil.getCurrentUser();
        String userId = user.getId();
        String sessionChatId = request.getSessionChatId();

        log.info("Send lucky money for user {} by wallet {}", user.getId(), walletId);

        luckyMoneyServiceUtil.checkUserInSession(user.getId(), request.getSessionChatId());

        long amount = luckyMoneyServiceUtil.transferMoneyFromUser(userId, walletId, request.getSoftToken(), request.getMessage());

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
        luckyMoneyServiceUtil.sendMessageLuckyMoney(userId, sessionChatId, request.getMessage(), luckyMoney.getId(), createdAt);
        log.info("Send message lucky money success");
    }

    @Override
    @Transactional(noRollbackFor = ErrCallApiException.class)
    public void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException {
        User user = luckyMoneyServiceUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LuckyMoney luckyMoney = luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())
                .orElseThrow(InvalidLuckyMoneyException::new);
        log.info("User {} receive lucky money {}", user.getId(), luckyMoney.getId());

        luckyMoneyServiceUtil.checkUserInSession(user.getId(), luckyMoney.getSessionChatId());
        luckyMoneyServiceUtil.checkExpiredOfLuckyMoney(luckyMoney.getExpiredAt(), now);
        luckyMoneyServiceUtil.checkOutOfBag(luckyMoney.getRestBag());
        luckyMoneyServiceUtil.checkUserHadReceived(luckyMoney.getId(), user.getId());


        long restMoney = luckyMoney.getRestMoney();
        int restBag = luckyMoney.getRestBag();
        long amount = luckyMoneyServiceUtil.calculateAmountLuckyMoney(restMoney, restBag, luckyMoney.getAmount(), luckyMoney.getNumberBag(), luckyMoney.getType());
        String message = String.format("User %s receive %d from lucky money %d", user.getId(), amount, luckyMoney.getId());
        luckyMoneyServiceUtil.transferMoneyToUser(luckyMoney.getSystemWalletId(), user.getId(), amount, message);

        luckyMoney.setRestMoney(restMoney - amount);
        luckyMoney.setRestBag(restBag - 1);

        luckyMoneyRepository.save(luckyMoney);

        ReceivedLuckyMoney receivedLuckyMoney = ReceivedLuckyMoney.builder()
                .luckyMoneyId(luckyMoney.getId())
                .receiverId(user.getId())
                .amount(amount)
                .createdAt(now).build();

        receivedLuckyMoneyRepository.save(receivedLuckyMoney);
        luckyMoneyServiceUtil.sendMessageReceiveLuckyMoney(user.getId(), luckyMoney.getSessionChatId(), luckyMoney.getId(), amount, luckyMoney.getWishMessage(), now);

    }

    @Override
    public List<LuckyMoneyDTO> getAllLuckyMoney(String sessionId) {
        User user = luckyMoneyServiceUtil.getCurrentUser();
        log.info("User {} get all lucky money of chat group {}", user.getId(), sessionId);

        luckyMoneyServiceUtil.checkUserInSession(user.getId(), sessionId);

        List<LuckyMoney> luckyMoneyList = luckyMoneyRepository.findAllBySessionChatId(sessionId);

        return luckyMoneyServiceUtil.luckyMoneyList2LuckyMoneyDTOList(luckyMoneyList, user);

    }

    @Override
    public LuckyMoneyDetails getDetailsLuckyMoney(long luckyMoneyId) throws InvalidLuckyMoneyException {
        log.info("Get details of lucky money {}", luckyMoneyId);
        User user = luckyMoneyServiceUtil.getCurrentUser();
        LuckyMoney luckyMoney = luckyMoneyRepository.findLuckyMoneyById(luckyMoneyId)
                .orElseThrow(() -> {
                    log.error("Lucky money {} is not exists", luckyMoneyId);
                    return new InvalidLuckyMoneyException();
                });
        luckyMoneyServiceUtil.checkUserInSession(user.getId(), luckyMoney.getSessionChatId());

        LuckyMoneyDetails luckyMoneyDetails = luckyMoneyMapper.luckyMoney2LuckyMoneyDetails(luckyMoney);

        UserInfo userInfo = luckyMoneyServiceUtil.getUserInfo(luckyMoney.getUserId());

        List<UserReceiveInfo> listReceivedUsers = luckyMoneyServiceUtil.getListReceivedUsers(luckyMoney.getId());

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
                luckyMoneyServiceUtil.transferMoneyToUser(luckyMoney.getSystemWalletId(), luckyMoney.getUserId(), luckyMoney.getRestMoney(), String.format("Refund %d from lucky money %d for user %s", luckyMoney.getRestMoney(), luckyMoney.getId(), luckyMoney.getUserId()));
                luckyMoney.setRestMoney(0L);
                luckyMoneyRepository.save(luckyMoney);
            } catch (CannotTransferMoneyException exception) {
                log.error("Can't transfer money");
                luckyMoney.setExpiredAt(now.plusMinutes(10));
                luckyMoneyRepository.save(luckyMoney);
            }
        });
    }
}
