package mavmi.telegram_bot.common.httpFilter.userSession.session;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Setter
@Component
@RequestScope
@ConditionalOnProperty(prefix = "web-filter", name = "enabled", havingValue = "true")
public class UserSession {

    @Getter
    private Long id;
    @Getter
    private Boolean accessGranted;
    private UserDataCache cache;

    @SuppressWarnings("unchecked")
    public <T extends UserDataCache> T getCache() {
        return (T) cache;
    }
}
