package com.hey.lucky.validation.validator;

import com.hey.lucky.dto.user.CreateLuckyMoneyRequest;
import com.hey.lucky.validation.anotation.UserInSessionChat;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UserInSessionChatValidator implements ConstraintValidator<UserInSessionChat, CreateLuckyMoneyRequest> {

    @Override
    public boolean isValid(CreateLuckyMoneyRequest createLuckyMoneyRequest, ConstraintValidatorContext constraintValidatorContext) {
        return false;
    }
}
