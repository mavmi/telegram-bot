package mavmi.telegram_bot.common.httpFilter.session;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

/**
 * Contains information about current user's request
 */
@Setter
@Component
@RequestScope
@ConditionalOnProperty(prefix = "service.web-filter", name = "enabled", havingValue = "true")
public class UserSession {

    @Getter
    private Long id;
    @Getter
    private Boolean accessGranted;
    private AbstractUserDataCache cache;

    @SuppressWarnings("unchecked")
    public <T extends AbstractUserDataCache> T getCache() {
        return (T) cache;
    }
}
