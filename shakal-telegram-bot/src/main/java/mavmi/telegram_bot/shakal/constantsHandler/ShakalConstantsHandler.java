package mavmi.telegram_bot.shakal.constantsHandler;

import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Used to upload and store shakal bot's constants from json file
 */
@Component
public class ShakalConstantsHandler extends ConstantsHandler {

    public ShakalConstantsHandler(@Value("${constants-handler.working-file}") String filePath) {
        super(filePath, ShakalConstants.class);
    }

    public ShakalConstants get() {
        return (ShakalConstants) constants;
    }
}
