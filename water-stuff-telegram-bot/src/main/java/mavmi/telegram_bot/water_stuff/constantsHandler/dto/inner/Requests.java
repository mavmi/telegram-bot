package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Requests {
    @JsonProperty("get-full-info")
    private String getFullInfo;
    @JsonProperty("get-group")
    private String getGroup;
    private String add;
    private String cancel;
}
