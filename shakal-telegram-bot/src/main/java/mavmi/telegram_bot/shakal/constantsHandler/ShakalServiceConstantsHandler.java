package mavmi.telegram_bot.shakal.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.shakal.constantsHandler.dto.ShakalServiceConstants;
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
