package mavmi.telegram_bot.lib.user_cache_starter.aop.impl;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.AuthCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.cacheInitializer.api.CacheInitializer;
import mavmi.telegram_bot.lib.user_cache_starter.menu.container.CacheContainer;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * AOP processor for {@link SetupUserCaches}
 */
@Aspect
@Order(2)
@Component
@RequiredArgsConstructor
public class SetupUserCacheProcessor {

    private final CacheInitializer cacheInitializer;
    private final CacheContainer cacheContainer;
    private final UserCachesProvider userCachesProvider;

    @Around("@annotation(setupUserCaches)")
    public Object process(ProceedingJoinPoint joinPoint, SetupUserCaches setupUserCaches) throws Throwable {
        long chatId = ((ServiceRequest) joinPoint.getArgs()[0]).getChatId();

        UserCaches userCaches = userCachesProvider.get();
        userCaches.setDataCache(cacheContainer.getDataCacheByUserId(chatId, DataCache.class));
        userCaches.setAuthCache(cacheContainer.getAuthCacheByUserId(chatId, AuthCache.class));

        DataCache dataCache = userCaches.getDataCache(DataCache.class);
        AuthCache authCache = userCaches.getAuthCache(AuthCache.class);

        if (dataCache == null) {
            dataCache = cacheInitializer.initDataCache(chatId);
            userCaches.setDataCache(dataCache);
            cacheContainer.putDataCache(chatId, dataCache);
        }
        if (authCache == null) {
            authCache = cacheInitializer.initAuthCache(chatId);
            userCaches.setAuthCache(authCache);
            cacheContainer.putAuthCache(chatId, authCache);
        }

        return joinPoint.proceed();
    }


}
