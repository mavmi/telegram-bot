package mavmi.telegram_bot.monitoring.cache.dto.inner;

import lombok.Getter;
import lombok.Setter;
import mavmi.parameters_management_system.common.parameter.impl.Parameter;

@Getter
@Setter
public class PmsCache {
    private Parameter selectedParameter;
}
