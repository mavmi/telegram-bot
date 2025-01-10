package mavmi.telegram_bot.water_stuff.constantsHandler;

import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store water stuff bot's constants from json file
 */
@Component
public class WaterConstantsHandler extends ConstantsHandler {

    public WaterConstantsHandler(@Value("${constants-handler.working-file}") String filePath) {
        super(filePath, WaterConstants.class);
    }

    public WaterConstants get() {
        return (WaterConstants) constants;
    }
}
