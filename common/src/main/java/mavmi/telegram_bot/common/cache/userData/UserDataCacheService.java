package mavmi.telegram_bot.common.cache.userData;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "cache.user-data", name = "enabled", havingValue = "true")
public class UserDataCacheService {

    protected Cache<Long, AbstractUserDataCache> userIdToCache;

    public UserDataCacheService(@Value("${cache.user-data.expire}") Long duration ) {
        userIdToCache = Caffeine
                .newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .build();
    }

    public void put(Long id, AbstractUserDataCache userDataCache) {
        userIdToCache.put(id, userDataCache);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public<T extends AbstractUserDataCache> T get(Long chatId) {
        return (T) userIdToCache.getIfPresent(chatId);
    }


}
