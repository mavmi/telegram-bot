package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    @JsonProperty("yes")
    private String yes;
    @JsonProperty("no")
    private String no;
    @JsonProperty("exit")
    private String exit;
}
