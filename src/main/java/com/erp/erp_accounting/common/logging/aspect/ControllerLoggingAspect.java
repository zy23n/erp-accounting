package com.erp.erp_accounting.common.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restController() {}

    @Before("restController()")
    public void logControllerEntry(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("[CONTROLLER] action=ENTER, controller={}, method={}", className, methodName);
    }
}
