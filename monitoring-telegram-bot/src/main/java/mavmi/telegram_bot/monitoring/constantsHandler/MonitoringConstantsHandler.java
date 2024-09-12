package mavmi.telegram_bot.monitoring.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store monitoring bot's constants from json file
 */
@Component
@RequiredArgsConstructor
public class MonitoringConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private MonitoringConstants constants;

    public MonitoringConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(MonitoringConstants.class);
        }

        return constants;
    }

}
