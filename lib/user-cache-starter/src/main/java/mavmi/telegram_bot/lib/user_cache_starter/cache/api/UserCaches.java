package mavmi.telegram_bot.lib.user_cache_starter.cache.api;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-scope container of user's caches
 */
@Slf4j
@Getter
@Setter
@Component("userCaches")
@Scope("thread")
public class UserCaches {

    private static AtomicInteger ID = new AtomicInteger();
    private final int id = ID.incrementAndGet();

    public UserCaches() {
        log.info("New instance of UserCaches beans with id {}", id);
    }

    private AuthCache authCache;
    private DataCache dataCache;

    public <T extends AuthCache> T getAuthCache(Class<T> cls) {
        return (T) authCache;
    }

    public <T extends DataCache> T getDataCache(Class<T> cls) {
        return (T) dataCache;
    }
}
