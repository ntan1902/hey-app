package com.hey.lucky.dto.user;

import com.hey.lucky.dto.auth_service.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LuckyMoneyDetails {
    private String wishMessage; //
    private UserInfo userCreated;
    private List<UserReceiveInfo> usersReceived;
    private int totalBag; //
    private int restBag; //
    private String type; //
    private long restMoney; //
    private long totalMoney; //
}
