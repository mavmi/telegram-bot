package mavmi.telegram_bot.common.constantsHandler.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mavmi.telegram_bot.common.constantsHandler.api.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * Is used to store constants such as buttons and default replies
 */
@Component
public class ConstantsHandler {

    private final File workingFile;

    @SneakyThrows
    public ConstantsHandler(@Value("${constants-handler.working-file}") String filePath) {
        this.workingFile = new File(filePath);
    }

    @SneakyThrows
    public <T extends Constants> T get(Class<T> cls) {
        return new ObjectMapper().readValue(workingFile, cls);
    }
}
