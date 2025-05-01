package mavmi.telegram_bot.monitoring.constantsHandler;

import mavmi.telegram_bot.lib.constants_handler.impl.ConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store monitoring bot's constants from json file
 */
@Component
public class MonitoringConstantsHandler extends ConstantsHandler {

    public MonitoringConstantsHandler(@Value("${constants-handler.working-file}") String filePath) {
        super(filePath, MonitoringConstants.class);
    }

    public MonitoringConstants get() {
        return (MonitoringConstants) constants;
    }
}
