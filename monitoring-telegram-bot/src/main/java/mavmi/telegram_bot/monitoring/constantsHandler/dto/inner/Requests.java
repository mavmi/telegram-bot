package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Requests {
    @JsonProperty("host")
    private String host;
    @JsonProperty("apps")
    private String apps;
}
