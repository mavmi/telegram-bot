package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Qr {
    @JsonProperty("qr_is_creating_response")
    private String qrIsCreatingResponse;
    @JsonProperty("requests_timeout")
    private String requestsTimeout;
}
