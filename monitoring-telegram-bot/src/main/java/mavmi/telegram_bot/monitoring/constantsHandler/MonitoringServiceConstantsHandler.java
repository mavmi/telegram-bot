package mavmi.telegram_bot.monitoring.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringServiceConstants;
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
