package mavmi.telegram_bot.common.aop.cache.impl;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.cache.impl.CacheContainer;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.service.service.Service;
import mavmi.telegram_bot.common.service.service.dto.ServiceRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Handler for {@link SetupUserCaches}
 */
@Aspect
@Order(2)
@Component
@RequiredArgsConstructor
public class SetupUserCacheAopProcessor {

    private final Service service;
    private final CacheContainer cacheContainer;

    @Autowired
    private CacheComponent cacheComponent;

    @Around("@annotation(setupUserCaches)")
    public Object process(ProceedingJoinPoint joinPoint, SetupUserCaches setupUserCaches) throws Throwable {
        long chatId = ((ServiceRequest) joinPoint.getArgs()[0]).getChatId();
        CacheContainer.CachesBucket cachesBucket = cacheContainer.getAllCachesByUserId(chatId);
        DataCache dataCache = cachesBucket.getDataCache(DataCache.class);
        AuthCache authCache = cachesBucket.getAuthCache(AuthCache.class);

        if (dataCache == null) {
            dataCache = service.initDataCache(chatId);
            cachesBucket.setDataCache(dataCache);
            cacheContainer.putDataCache(chatId, dataCache);
        }
        if (authCache == null) {
            authCache = service.initAuthCache(chatId);
            cachesBucket.setAuthCache(authCache);
            cacheContainer.putAuthCache(chatId, authCache);
        }

        cacheComponent.setCacheBucket(cachesBucket);
        return joinPoint.proceed();
    }


}
