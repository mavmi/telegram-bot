package mavmi.telegram_bot.rocketchat.constantsHandler;

import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store rocketchat bot's constants from json file
 */
@Component
public class RocketConstantsHandler extends ConstantsHandler {

    public RocketConstantsHandler(@Value("${constants-handler.working-file}") String filePath) {
        super(filePath, RocketConstants.class);
    }

    public RocketConstants get() {
        return (RocketConstants) constants;
    }
}
