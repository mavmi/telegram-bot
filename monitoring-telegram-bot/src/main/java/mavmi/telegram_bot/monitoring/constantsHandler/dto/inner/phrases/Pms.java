package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pms {
    @JsonProperty("invalid_param_name")
    private String invalidParamName;
    @JsonProperty("enter_new_value")
    private String enterNewValue;
}
