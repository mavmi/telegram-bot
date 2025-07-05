package mavmi.telegram_bot.monitoring.constantsHandler.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.constants_handler.api.Constants;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.Phrases;

/**
 * Container of bot's constants
 */
@Getter
@Setter
public class MonitoringConstants implements Constants {
    private Phrases phrases;
}
