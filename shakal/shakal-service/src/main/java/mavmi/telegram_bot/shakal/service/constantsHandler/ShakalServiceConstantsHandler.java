package mavmi.telegram_bot.shakal.service.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.ConstantsHandler;
import mavmi.telegram_bot.shakal.service.constantsHandler.dto.ShakalServiceConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShakalServiceConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private ShakalServiceConstants constants;

    public ShakalServiceConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(ShakalServiceConstants.class);
        }

        return constants;
    }
}
