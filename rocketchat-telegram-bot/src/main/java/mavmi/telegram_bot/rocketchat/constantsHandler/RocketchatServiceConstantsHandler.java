package mavmi.telegram_bot.rocketchat.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RocketchatServiceConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private RocketchatServiceConstants constants;

    public RocketchatServiceConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(RocketchatServiceConstants.class);
        }

        return constants;
    }
}
