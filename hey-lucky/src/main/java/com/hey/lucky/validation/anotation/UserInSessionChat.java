package com.hey.lucky.validation.anotation;

import com.hey.lucky.validation.validator.UserInSessionChatValidator;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserInSessionChatValidator.class)
public @interface UserInSessionChat {
}
