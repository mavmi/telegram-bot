package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    @JsonProperty("exit")
    private String exit;
}
