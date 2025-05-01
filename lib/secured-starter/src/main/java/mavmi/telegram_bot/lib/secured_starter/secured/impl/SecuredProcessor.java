package mavmi.telegram_bot.lib.secured_starter.secured.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
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

    private final UserCachesProvider userCachesProvider;

    @Around("@annotation(secured)")
    public Object process(ProceedingJoinPoint joinPoint, Secured secured) throws Throwable {
        UserCaches userCaches = userCachesProvider.get();
        long chatId = userCaches.getDataCache(DataCache.class).getUserId();

        if (userCaches.getAuthCache(AuthCache.class).isAccessGranted()) {
            log.info("Access granted for id {}", chatId);
            return joinPoint.proceed();
        } else {
            log.warn("User unauthorized: id {}", chatId);
            return null;
        }
    }
}
