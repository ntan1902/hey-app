package com.hey.lucky.service;

import com.hey.lucky.api.AuthApi;
import com.hey.lucky.dto.auth_service.UserInfo;
import com.hey.lucky.dto.chat_service.LuckyMoneyMessageContent;
import com.hey.lucky.dto.chat_service.ReceiveLuckyMoneyMessageContent;
import com.hey.lucky.dto.payment_service.TransferFromUserRequest;
import com.hey.lucky.dto.payment_service.TransferToUserRequest;
import com.hey.lucky.dto.user.*;
import com.hey.lucky.entity.LuckyMoney;
import com.hey.lucky.entity.ReceivedLuckyMoney;
import com.hey.lucky.entity.User;
import com.hey.lucky.exception_handler.exception.*;
import com.hey.lucky.mapper.LuckyMoneyMapper;
import com.hey.lucky.repository.LuckyMoneyRepository;
import com.hey.lucky.repository.ReceivedLuckyMoneyRepository;
import com.hey.lucky.shared_data.WalletsInfo;
import com.hey.lucky.util.*;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.hey.lucky.util.LuckyMoneyServiceUtilImpl.MIN_AMOUNT;

@Service
@Log4j2
@NoArgsConstructor
public class LuckyMoneyServiceImpl implements LuckyMoneyService {

    private static final String UNAUTHORIZED= "Unauthorized!";

    private LuckyMoneyRepository luckyMoneyRepository;
    private ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository;
    private LuckyMoneyMapper luckyMoneyMapper;
    private LuckyMoneyServiceUtil luckyMoneyServiceUtil;
    private WalletsInfo walletsInfo;
    private UserUtil userUtil;
    private PaymentUtil paymentUtil;
    private ChatUtil chatUtil;
    private AuthApi authApi;

    @Autowired
    public LuckyMoneyServiceImpl(LuckyMoneyRepository luckyMoneyRepository,
                                 ReceivedLuckyMoneyRepository receivedLuckyMoneyRepository,
                                 LuckyMoneyMapper luckyMoneyMapper, LuckyMoneyServiceUtilImpl luckyMoneyServiceUtil, WalletsInfo walletsInfo, UserUtil userUtil, PaymentUtil paymentUtil, ChatUtil chatUtil, AuthApi authApi) {
        this.luckyMoneyRepository = luckyMoneyRepository;
        this.receivedLuckyMoneyRepository = receivedLuckyMoneyRepository;
        this.luckyMoneyMapper = luckyMoneyMapper;
        this.luckyMoneyServiceUtil = luckyMoneyServiceUtil;
        this.walletsInfo = walletsInfo;
        this.userUtil = userUtil;
        this.paymentUtil = paymentUtil;
        this.chatUtil = chatUtil;
        this.authApi = authApi;
    }

    @Override
    @Transactional(noRollbackFor = ErrCallChatApiException.class, rollbackFor = {CannotTransferMoneyException.class})
    public void createLuckyMoney(CreateLuckyMoneyRequest request) throws ErrCallApiException, CannotTransferMoneyException, ErrCallChatApiException, UserNotInSessionChatException, MinAmountPerBagException {
        long walletId = walletsInfo.getCurrentWallet();
        User user = userUtil.getCurrentUser();

        // Check amount is negative
        if (request.getAmount()/request.getNumberBag() < MIN_AMOUNT) {
            throw new MinAmountPerBagException("Min amount per bag is " + MIN_AMOUNT);
        }

        log.info("Send lucky money for user {} by wallet {}", user.getId(), walletId);
        // check user in session chat
        if (!userUtil.isUserInSession(user.getId(), request.getSessionChatId())) {
            log.info("User {} isn't in group chat {}", user.getId(), request.getSessionChatId());
            throw new UserNotInSessionChatException("You aren't in that group chat");
        }

        // transfer money from user
        long amount = paymentUtil.transferMoneyFromUser(
                TransferFromUserRequest.builder()
                        .userId(user.getId())
                        .message(request.getMessage())
                        .softToken(request.getSoftToken())
                        .walletId(walletId)
                        .amount(request.getAmount())
                        .build()
        );

        try {
            log.info("Amount: {}", amount);
            LocalDateTime createdAt = LocalDateTime.now();

            LuckyMoney luckyMoney = LuckyMoney.builder()
                    .userId(user.getId())
                    .systemWalletId(walletId)
                    .sessionChatId(request.getSessionChatId())
                    .amount(amount)
                    .restMoney(amount)
                    .numberBag(request.getNumberBag())
                    .restBag(request.getNumberBag())
                    .type(request.getType())
                    .wishMessage(request.getMessage())
                    .createdAt(createdAt)
                    // time expired
                    .expiredAt(createdAt.plusMinutes(7))
                    .build();
            luckyMoneyRepository.save(luckyMoney);

            log.info("Send message lucky money");
            chatUtil.sendMessageLuckyMoney(
                    LuckyMoneyMessageContent.builder()
                            .userId(luckyMoney.getUserId())
                            .sessionId(luckyMoney.getSessionChatId())
                            .message(luckyMoney.getWishMessage())
                            .luckyMoneyId(luckyMoney.getId())
                            .createdAt(luckyMoney.getCreatedAt().toString())
                            .build()
            );
        } catch (ErrCallChatApiException exception) {
            throw exception;
        } catch (Exception exception) { // save lucky money have error
            // refund money for user
            paymentUtil.transferMoneyToUser(TransferToUserRequest.builder()
                    .amount(amount)
                    .receiverId(user.getId())
                    .message("Refund")
                    .walletId(walletId)
                    .build());
        }

        log.info("Send message lucky money success");
    }

    @Override
    @Transactional(noRollbackFor = ErrCallChatApiException.class, rollbackFor = {CannotTransferMoneyException.class})
    public void receiveLuckyMoney(ReceiveLuckyMoneyRequest request) throws InvalidLuckyMoneyException, ErrCallApiException, LuckyMoneyExpiredException, CannotTransferMoneyException, OutOfBagException, HadReceivedException, ErrCallChatApiException, UserNotInSessionChatException {
        User user = userUtil.getCurrentUser();
        LocalDateTime now = LocalDateTime.now();
        LuckyMoney luckyMoney = luckyMoneyRepository.getLuckyMoneyById(request.getLuckyMoneyId())
                .orElseThrow(InvalidLuckyMoneyException::new);
        log.info("User {} receive lucky money {}", user.getId(), luckyMoney.getId());

        if (!userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())) {
            log.info("User {} isn't in group chat {}", user.getId(), luckyMoney.getSessionChatId());
            throw new UserNotInSessionChatException("You aren't in that group chat");
        }

        if (luckyMoney.isExpired(now)) {
            log.info("Lucky money {} is expired", luckyMoney.getId());
            throw new LuckyMoneyExpiredException();
        }

        if (luckyMoneyServiceUtil.hadUserReceived(luckyMoney.getId(), user.getId())) {
            log.info("User {} has received lucky money {}", user.getId(), luckyMoney.getId());
            throw new HadReceivedException();
        }

        if (luckyMoney.isOutOfBag()) {
            log.info("Lucky money {} is out of bag", luckyMoney.getId());
            throw new OutOfBagException();
        }

        long amount = luckyMoneyServiceUtil.calculateAmountLuckyMoney(luckyMoney);

        String message = String.format("User %s receive %d from lucky money %d", user.getId(), amount, luckyMoney.getId());
        // update rest bag and rest money
        long newRestMoney = luckyMoney.getRestMoney() - amount;
        int newRestBag = luckyMoney.getRestBag() - 1;
        luckyMoney.setRestMoney(newRestMoney);
        luckyMoney.setRestBag(newRestBag);
        luckyMoneyRepository.save(luckyMoney);

        ReceivedLuckyMoney receivedLuckyMoney = ReceivedLuckyMoney.builder()
                .luckyMoneyId(luckyMoney.getId())
                .receiverId(user.getId())
                .amount(amount)
                .createdAt(now).build();
        receivedLuckyMoneyRepository.save(receivedLuckyMoney);

        // transfer to user after do every thing
        paymentUtil.transferMoneyToUser(
                TransferToUserRequest.builder()
                        .walletId(luckyMoney.getSystemWalletId())
                        .receiverId(user.getId())
                        .amount(amount)
                        .message(message)
                        .build()
        );

        chatUtil.sendMessageReceiveLuckyMoney(
                ReceiveLuckyMoneyMessageContent.builder()
                        .receiverId(user.getId())
                        .sessionId(luckyMoney.getSessionChatId())
                        .luckyMoneyId(luckyMoney.getId())
                        .amount(amount)
                        .message(luckyMoney.getWishMessage())
                        .createdAt(now.toString()).build()
        );

    }

    @Override
    public List<LuckyMoneyDTO> getAllLuckyMoneyOfSession(String sessionId) throws ErrCallApiException, CannotGetUserInfo, UserNotInSessionChatException {
        User user = userUtil.getCurrentUser();
        log.info("User {} get all lucky money of chat group {}", user.getId(), sessionId);
        if ("-1".equals(sessionId)) {
            return new ArrayList<>();
        }
        if (!userUtil.isUserInSession(user.getId(), sessionId)) {
            log.info("User {} isn't in group chat {}", user.getId(), sessionId);
            throw new UserNotInSessionChatException("You aren't in that group chat");
        }

        List<LuckyMoney> luckyMoneyList = luckyMoneyRepository.findAllBySessionChatId(sessionId);

        return luckyMoneyServiceUtil.luckyMoneyList2LuckyMoneyDTOList(luckyMoneyList, user);

    }

    @Override
    public LuckyMoneyDetails getLuckyMoneyDetails(long luckyMoneyId) throws InvalidLuckyMoneyException, CannotGetUserInfo, ErrCallApiException, UserNotInSessionChatException {
        log.info("Get details of lucky money {}", luckyMoneyId);
        User user = userUtil.getCurrentUser();
        LuckyMoney luckyMoney = luckyMoneyRepository.findLuckyMoneyById(luckyMoneyId)
                .orElseThrow(() -> {
                    log.error("Lucky money {} is not exists", luckyMoneyId);
                    return new InvalidLuckyMoneyException();
                });

        if (!userUtil.isUserInSession(user.getId(), luckyMoney.getSessionChatId())) {
            log.info("User {} isn't in group chat {}", user.getId(), luckyMoney.getSessionChatId());
            throw new UserNotInSessionChatException("You aren't in that group chat");
        }

        LuckyMoneyDetails luckyMoneyDetails = luckyMoneyMapper.luckyMoney2LuckyMoneyDetails(luckyMoney);

        UserInfo senderLuckyMoney = userUtil.getUserInfo(luckyMoney.getUserId());

        List<UserReceivedInfo> listReceivedUsers = luckyMoneyServiceUtil.getListReceivedUsers(luckyMoney.getId());

        luckyMoneyDetails.setUserCreated(senderLuckyMoney);
        luckyMoneyDetails.setUsersReceived(listReceivedUsers);

        return luckyMoneyDetails;

    }

    @Override
    @Transactional
    public void refundLuckyMoney() {
        log.info("refund");
        LocalDateTime now = LocalDateTime.now();
        List<LuckyMoney> luckyMoneyList = luckyMoneyRepository.getAllByRestMoneyGreaterThanZeroAndExpiredAtBetween(now);
        luckyMoneyList.forEach(luckyMoney -> {
            log.info("Return lucky money {} with rest {} for user {}", luckyMoney.getId(), luckyMoney.getRestMoney(), luckyMoney.getUserId());
            long restMoney = luckyMoney.getRestMoney();
            try {

                luckyMoney.setRestMoney(0L);
                luckyMoneyRepository.save(luckyMoney);

                // return lucky money
                paymentUtil.transferMoneyToUser(
                        TransferToUserRequest.builder()
                                .walletId(luckyMoney.getSystemWalletId())
                                .receiverId(luckyMoney.getUserId())
                                .amount(luckyMoney.getRestMoney())
                                .message(String.format("Refund %d from lucky money %d for user %s", luckyMoney.getRestMoney(), luckyMoney.getId(), luckyMoney.getUserId()))
                                .build()
                );

            } catch (CannotTransferMoneyException exception) {
                log.error("Can't transfer money");
                luckyMoney.setExpiredAt(now.plusMinutes(10));
                luckyMoney.setRestMoney(restMoney);
                luckyMoneyRepository.save(luckyMoney);
            }
        });
    }
}
