package mavmi.telegram_bot.water_stuff.constantsHandler;

import mavmi.telegram_bot.lib.constants_handler.impl.ConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store water stuff bot's constants from json file
 */
@Component
public class WaterConstantsHandler extends ConstantsHandler {

    public WaterConstantsHandler(@Value("classpath:/constants/config.json") Resource configFileResource) {
        super(configFileResource, WaterConstants.class);
    }

    public WaterConstants get() {
        return (WaterConstants) constants;
    }
}
