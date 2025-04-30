package com.example.be12fin5verdosewmthisbe.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Aspect
@Component
public class DataSourceAspect {

    @Before("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void beforeTransactional(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Transactional transactional = signature.getMethod().getAnnotation(Transactional.class);

        if (transactional.readOnly()) {
            DataSourceContextHolder.setDataSourceKey("READ");
        } else {
            DataSourceContextHolder.setDataSourceKey("WRITE");
        }
    }

    @After("@annotation(org.springframework.transaction.annotation.Transactional)")
    public void afterTransactional() {
        DataSourceContextHolder.clearDataSourceKey();
    }
}
