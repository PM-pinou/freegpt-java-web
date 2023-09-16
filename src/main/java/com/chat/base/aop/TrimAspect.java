package com.chat.base.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class TrimAspect {

    @Around(value = "@annotation(Trimmed)")
    public Object trim(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                args[i] = ((String) args[i]).trim();
            }
        }
        return joinPoint.proceed(args);
    }
}
