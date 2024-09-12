package mavmi.telegram_bot.common.aop.secured.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP processor for {@link Secured}
 */
@Slf4j
@Aspect
@Order(3)
@Component
@RequiredArgsConstructor
public class SecuredProcessor {

    @Autowired
    private CacheComponent cacheComponent;

    @Around("@annotation(secured)")
    public Object process(ProceedingJoinPoint joinPoint, Secured secured) throws Throwable {
        long chatId = cacheComponent.getCacheBucket().getDataCache(DataCache.class).getUserId();

        if (cacheComponent.getCacheBucket().getAuthCache(AuthCache.class).isAccessGranted()) {
            log.info("Access granted for id {}", chatId);
            return joinPoint.proceed();
        } else {
            log.warn("User unauthorized: id {}", chatId);
            return null;
        }
    }
}
