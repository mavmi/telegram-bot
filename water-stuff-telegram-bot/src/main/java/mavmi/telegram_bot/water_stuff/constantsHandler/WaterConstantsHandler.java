package mavmi.telegram_bot.water_stuff.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store water stuff bot's constants from json file
 */
@Component
@RequiredArgsConstructor
public class WaterConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private WaterConstants constants;

    public WaterConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(WaterConstants.class);
        }

        return constants;
    }
}
