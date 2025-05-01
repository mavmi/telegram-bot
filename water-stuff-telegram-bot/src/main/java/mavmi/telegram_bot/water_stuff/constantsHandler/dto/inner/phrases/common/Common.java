package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.phrases.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Common {
    @JsonProperty("success")
    private String success;
    @JsonProperty("error")
    private String error;
    @JsonProperty("approve")
    private String approve;
    @JsonProperty("operation_canceled")
    private String operationCanceled;
}
