package mavmi.telegram_bot.shakal.service.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    private String greetings;
    private String apolocheese;
    private String error;
    @JsonProperty("invalid-input")
    private String invalidInput;
}
