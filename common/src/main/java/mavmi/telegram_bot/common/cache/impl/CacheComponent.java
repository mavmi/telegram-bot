package mavmi.telegram_bot.common.cache.impl;

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
public class CacheComponent {
    private CacheContainer.CachesBucket cacheBucket;
}
