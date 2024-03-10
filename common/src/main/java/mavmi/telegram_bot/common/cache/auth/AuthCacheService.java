package mavmi.telegram_bot.common.cache.auth;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "cache.auth", name = "enabled", havingValue = "true")
public class AuthCacheService {

    private final Cache<Long, Boolean> authCache;

    public AuthCacheService(@Value("${cache.auth.expire}") Long expDuration) {
        this.authCache = Caffeine
                .newBuilder()
                .expireAfterWrite(expDuration, TimeUnit.MINUTES)
                .build();
    }

    public void put(Long id, Boolean value) {
        authCache.put(id, value);
    }

    @Nullable
    public Boolean get(Long id) {
        return authCache.getIfPresent(id);
    }

}
