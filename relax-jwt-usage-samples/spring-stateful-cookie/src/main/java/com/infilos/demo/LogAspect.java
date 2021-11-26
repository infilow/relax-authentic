package com.infilos.demo;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;

@Slf4j
@Order(100)
@Aspect
public class LogAspect {
    @Pointcut("@annotation(com.infilos.demo.LogMark)")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("--------------------------------aop-------------------------------");
        return joinPoint.proceed();
    }
}
