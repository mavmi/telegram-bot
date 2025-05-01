package mavmi.telegram_bot.lib.constants_handler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mavmi.telegram_bot.lib.constants_handler.api.Constants;

import java.io.File;

/**
 * Is used to store constants such as buttons and default replies
 */
public class ConstantsHandler {

    protected final Constants constants;

    @SneakyThrows
    public <T extends Constants> ConstantsHandler(String filePath, Class<T> cls) {
        this.constants = new ObjectMapper().readValue(new File(filePath), cls);
    }
}
