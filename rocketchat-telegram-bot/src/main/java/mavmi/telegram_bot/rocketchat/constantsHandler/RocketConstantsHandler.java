package mavmi.telegram_bot.rocketchat.constantsHandler;

import mavmi.telegram_bot.lib.constants_handler.impl.ConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store rocketchat bot's constants from json file
 */
@Component
public class RocketConstantsHandler extends ConstantsHandler {

    public RocketConstantsHandler(@Value("classpath:/constants/config.json") Resource configFileResource) {
        super(configFileResource, RocketConstants.class);
    }

    public RocketConstants get() {
        return (RocketConstants) constants;
    }
}
