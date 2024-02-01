package mavmi.telegram_bot.common.httpFilter.session;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.Map;

/**
 * Содержит информацию о текущей сессии пользователя
 */
@Setter
@Component
@RequestScope
@ConditionalOnProperty(prefix = "service.web-filter", name = "enabled", havingValue = "true")
public class UserSession {

    private final Map<BOT_NAME, Boolean> accessAttributes = new HashMap<>();
    private final UserAuthentication userAuthentication;

    public UserSession(UserAuthentication userAuthentication) {
        this.userAuthentication = userAuthentication;
    }

    @Getter
    private Long id;

    public boolean getAccessAttribute() {
        return true;
    }
}
