package mavmi.telegram_bot.water_stuff.service.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.ConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WaterStuffServiceConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private WaterStuffServiceConstants constants;

    public WaterStuffServiceConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(WaterStuffServiceConstants.class);
        }

        return constants;
    }
}
