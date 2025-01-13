package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Apps {
    @JsonProperty("pk")
    private String pk;
    @JsonProperty("fp")
    private String fp;
    @JsonProperty("gc")
    private String gc;
}
