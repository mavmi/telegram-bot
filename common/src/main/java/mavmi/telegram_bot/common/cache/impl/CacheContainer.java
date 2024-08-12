package mavmi.telegram_bot.common.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.Builder;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * This bean is used to store services' caches,
 * such as last messages, selected menu, etc.
 */
@Component
@ConditionalOnProperty(
        prefix = "cache",
        name = {"data-cache.expiry-time-minutes", "auth-cache.expiry-time-minutes"}
)
public class CacheContainer {

    private final Cache<Long, DataCache> dataCache;
    private final Cache<Long, AuthCache> authCache;

    public CacheContainer(
            @Value("${cache.data-cache.expiry-time-minutes}") long dataCacheExpiry,
            @Value("${cache.auth-cache.expiry-time-minutes}") long authCacheExpiry
    ) {
        this.dataCache = Caffeine.newBuilder().expireAfterWrite(dataCacheExpiry, TimeUnit.MINUTES).build();
        this.authCache = Caffeine.newBuilder().expireAfterWrite(authCacheExpiry, TimeUnit.MINUTES).build();
    }

    public CachesBucket getAllCachesByUserId(long id) {
        return CachesBucket
                .builder()
                .dataCache(getDataCacheByUserId(id, DataCache.class ))
                .authCache(getAuthCacheByUserId(id, AuthCache.class ))
                .build();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends DataCache> T getDataCacheByUserId(long id, Class<T> cls) {
        return (T) dataCache.getIfPresent(id);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends AuthCache> T getAuthCacheByUserId(long id, Class<T> cls) {
        return (T) authCache.getIfPresent(id);
    }

    public void putDataCache(long id, DataCache dataCacheValue) {
        dataCache.put(id, dataCacheValue);
    }

    public void putAuthCache(long id, AuthCache authCacheValue) {
        authCache.put(id, authCacheValue);
    }

    /**
     * Secondary class to store all user's caches
     */
    @Setter
    @Builder
    public static class CachesBucket {
        private DataCache dataCache;
        private AuthCache authCache;

        @Nullable
        @SuppressWarnings("unchecked")
        public <T extends DataCache> T getDataCache(Class<T> cls) {
            return (T) dataCache;
        }

        @Nullable
        @SuppressWarnings("unchecked")
        public <T extends AuthCache> T getAuthCache(Class<T> cls) {
            return (T) authCache;
        }
    }
}
