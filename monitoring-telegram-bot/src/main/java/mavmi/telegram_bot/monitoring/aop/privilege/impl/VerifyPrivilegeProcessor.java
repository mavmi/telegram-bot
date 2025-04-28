package mavmi.telegram_bot.monitoring.aop.privilege.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
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

    private final CommonServiceModule commonServiceModule;

    @Autowired
    private UserCaches cacheComponent;

    @Around("@annotation(verifyPrivilege)")
    public Object process(ProceedingJoinPoint joinPoint, VerifyPrivilege verifyPrivilege) throws Throwable {
        MonitoringDataCache dataCache = cacheComponent.getDataCache(MonitoringDataCache.class);

        if (dataCache.getUserPrivileges().contains(verifyPrivilege.value())) {
            return joinPoint.proceed();
        } else {
            log.warn("User with id {} doesn't have required privilege \"{}\"", dataCache.getUserId(), verifyPrivilege.value().getName());
            MonitoringServiceRq request = (MonitoringServiceRq) joinPoint.getArgs()[0];
            commonServiceModule.sendCurrentMenuButtons(request.getChatId());
            return null;
        }
    }
}
