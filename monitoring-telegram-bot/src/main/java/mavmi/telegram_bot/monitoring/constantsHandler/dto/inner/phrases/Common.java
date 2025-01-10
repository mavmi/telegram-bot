package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    @JsonProperty("available-options")
    private String availableOptions;
    @JsonProperty("ok")
    private String ok;
    @JsonProperty("error")
    private String error;
}
