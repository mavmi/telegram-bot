package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    @JsonProperty("ok")
    private String ok;
    @JsonProperty("error")
    private String error;
    @JsonProperty("invalid-request")
    private String invalidRequest;
    @JsonProperty("unknown-command")
    private String unknownCommand;
}
