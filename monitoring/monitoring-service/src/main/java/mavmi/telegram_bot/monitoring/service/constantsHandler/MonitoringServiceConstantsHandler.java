package mavmi.telegram_bot.monitoring.service.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.ConstantsHandler;
import mavmi.telegram_bot.monitoring.service.constantsHandler.dto.MonitoringServiceConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringServiceConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private MonitoringServiceConstants constants;

    public MonitoringServiceConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(MonitoringServiceConstants.class);
        }

        return constants;
    }

}
