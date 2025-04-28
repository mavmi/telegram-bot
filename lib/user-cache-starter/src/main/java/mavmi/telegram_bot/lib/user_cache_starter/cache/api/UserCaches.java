package mavmi.telegram_bot.lib.user_cache_starter.cache.api;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Thread-scope container of user's caches
 */
@Getter
@Setter
@Component
@Scope("thread")
public class UserCaches {
    private AuthCache authCache;
    private DataCache dataCache;

    public <T extends AuthCache> T getAuthCache(Class<T> cls) {
        return (T) authCache;
    }

    public <T extends DataCache> T getDataCache(Class<T> cls) {
        return (T) dataCache;
    }
}
