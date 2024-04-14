package mavmi.telegram_bot.monitoring.service.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phrases {
    @JsonProperty("available-options")
    private String availableOptions;
    private String ok;
    private String error;
}
