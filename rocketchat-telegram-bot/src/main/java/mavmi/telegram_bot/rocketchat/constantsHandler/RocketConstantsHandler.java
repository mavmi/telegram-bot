package mavmi.telegram_bot.rocketchat.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketConstants;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store rocketchat bot's constants from json file
 */
@Component
@RequiredArgsConstructor
public class RocketConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private RocketConstants constants;

    public RocketConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(RocketConstants.class);
        }

        return constants;
    }
}
