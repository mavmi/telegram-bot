package mavmi.telegram_bot.common.cache.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * User's authentication cache data
 */
@Getter
@Setter
@AllArgsConstructor
public abstract class AuthCache {
    private boolean accessGranted;
}
