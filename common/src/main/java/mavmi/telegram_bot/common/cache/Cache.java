package mavmi.telegram_bot.common.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "service.cache", name = "enabled", havingValue = "true")
public class Cache<U extends AbstractUserCache> {

    protected com.github.benmanes.caffeine.cache.Cache<Long, U> userIdToCache;

    public Cache(@Value("${service.cache.expire}") Long duration ) {
        userIdToCache = Caffeine
                .newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .build();
    }

    public void putUser(U user) {
        userIdToCache.put(user.getUserId(), user);
    }

    @Nullable
    public U getUser(Long chatId) {
        return userIdToCache.getIfPresent(chatId);
    }


}
