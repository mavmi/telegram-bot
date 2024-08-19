package mavmi.telegram_bot.hb.constantsHandler;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.constantsHandler.impl.ConstantsHandler;
import mavmi.telegram_bot.hb.constantsHandler.dto.HbConstants;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HbConstantsHandler {

    private final ConstantsHandler constantsHandler;

    private HbConstants constants;

    public HbConstants get() {
        if (constants == null) {
            constants = constantsHandler.get(HbConstants.class);
        }

        return constants;
    }
}
