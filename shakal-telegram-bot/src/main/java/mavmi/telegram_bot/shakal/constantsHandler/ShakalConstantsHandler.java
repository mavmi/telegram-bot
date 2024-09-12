package mavmi.telegram_bot.shakal.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store shakal bot's constants from json file
 */
@Component
@RequiredArgsConstructor
public class ShakalConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private ShakalConstants constants;

    public ShakalConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(ShakalConstants.class);
        }

        return constants;
    }
}
