package com.hey.auth.service;

import com.hey.auth.entity.BlackList;
import com.hey.auth.jwt.JwtUserUtil;
import com.hey.auth.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class BlackListServiceImpl implements BlackListService{
    private final BlackListRepository blackListRepository;
    private final JwtUserUtil jwtUserUtil;

    @Override
    public Boolean isExistInBlackListToken(String token) {
        return blackListRepository.existsById(token);
    }

    @Override
    public void deleteExpiredTokenInBlackList() {
        log.info("Inside deleteExpiredTokenInBlackList of BlackListServiceImpl");
        List<BlackList> blackLists = (List<BlackList>) blackListRepository.findAll();

        blackLists.forEach(blackList -> {
            Date expiration = jwtUserUtil.getExpiration(blackList.getId());
            if(expiration.after(new Date(System.currentTimeMillis()))) {
                blackListRepository.delete(blackList);
            }
        });

    }

}
