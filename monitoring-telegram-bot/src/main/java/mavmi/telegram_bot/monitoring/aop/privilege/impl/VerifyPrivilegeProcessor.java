package mavmi.telegram_bot.monitoring.aop.privilege.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * AOP processor for {@link VerifyPrivilege}
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class VerifyPrivilegeProcessor {

    @Autowired
    private CacheComponent cacheComponent;

    @Around("@annotation(verifyPrivilege)")
    public Object process(ProceedingJoinPoint joinPoint, VerifyPrivilege verifyPrivilege) throws Throwable {
        MonitoringDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringDataCache.class);

        if (dataCache.getPrivileges().contains(verifyPrivilege.value())) {
            return joinPoint.proceed();
        } else {
            log.warn("User with id {} doesn't have required permission {}", dataCache.getUserId(), verifyPrivilege.value().getName());
            return null;
        }
    }
}
