package com.hey.payment.utils;

import com.hey.payment.entity.System;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SystemUtilImpl implements SystemUtil {
    @Override
    public System getCurrentSystem() {
        return (System) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
