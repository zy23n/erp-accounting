package com.erp.erp_accounting.common.logging.aspect;

import com.erp.erp_accounting.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("execution(* com.erp.erp_accounting..service..*(..))")
    public void serviceLayer() {}

    @Around("serviceLayer()")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - start;
            log.info(
                    "[SERVICE OK] service={}, method={} ({}ms)",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    duration
            );
            return result;

        } catch (BusinessException e) {
            throw e;

        } catch (Exception e) {
            log.debug(
                    "[SERVICE SYSTEM ERROR] service={}, method={}",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    e
            );
            throw e;
        }
    }
}
