package mavmi.telegram_bot.common.constantsHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import mavmi.telegram_bot.common.constantsHandler.dto.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConditionalOnProperty(prefix = "constants-handler", name = "enabled", havingValue = "true")
public class ConstantsHandler {

    private final File workingFile;

    @SneakyThrows
    public ConstantsHandler(@Value("${constants-handler.working-file}") String fileName) {
        this.workingFile = new File(fileName);
    }

    @SneakyThrows
    public <T extends Constants> T get(Class<T> cls) {
        return new ObjectMapper().readValue(workingFile, cls);
    }
}
