package mavmi.telegram_bot.monitoring.service.constantsHandler.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.constantsHandler.dto.Constants;
import mavmi.telegram_bot.monitoring.service.constantsHandler.dto.inner.Buttons;
import mavmi.telegram_bot.monitoring.service.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.monitoring.service.constantsHandler.dto.inner.Requests;

@Getter
@Setter
public class MonitoringServiceConstants implements Constants {
    private Buttons buttons;
    private Phrases phrases;
    private Requests requests;
}
