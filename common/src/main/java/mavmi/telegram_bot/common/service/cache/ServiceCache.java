package mavmi.telegram_bot.common.service.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "service.cache", name = "enabled", havingValue = "true")
public class ServiceCache<U extends AbsUserCache> {

    protected Cache<Long, U> idToUser;

    public ServiceCache(@Value("${service.cache.expire}") Long duration ) {
        idToUser = Caffeine
                .newBuilder()
                .expireAfterWrite(duration, TimeUnit.MINUTES)
                .build();
    }

    public void putUser(U user) {
        idToUser.put(user.getUserId(), user);
    }

    @Nullable
    public U getUser(Long chatId) {
        return idToUser.getIfPresent(chatId);
    }


}
