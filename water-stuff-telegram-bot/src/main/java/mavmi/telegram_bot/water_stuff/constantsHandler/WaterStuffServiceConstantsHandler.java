package mavmi.telegram_bot.water_stuff.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
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
