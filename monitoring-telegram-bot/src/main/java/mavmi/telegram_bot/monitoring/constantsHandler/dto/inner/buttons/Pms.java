package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pms {
    @JsonProperty("info")
    private String info;
    @JsonProperty("new_value")
    private String newValue;
}
