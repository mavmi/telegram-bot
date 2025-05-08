package mavmi.telegram_bot.shakal.constantsHandler;

import mavmi.telegram_bot.lib.constants_handler.impl.ConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store shakal bot's constants from json file
 */
@Component
public class ShakalConstantsHandler extends ConstantsHandler {

    public ShakalConstantsHandler(@Value("classpath:/constants/config.json") Resource configFileResource) {
        super(configFileResource, ShakalConstants.class);
    }

    public ShakalConstants get() {
        return (ShakalConstants) constants;
    }
}
