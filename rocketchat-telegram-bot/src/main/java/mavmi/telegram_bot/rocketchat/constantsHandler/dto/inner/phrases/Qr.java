package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Qr {
    @JsonProperty("qr-is-creating-response")
    private String qrIsCreatingResponse;
    @JsonProperty("requests-timeout")
    private String requestsTimeout;
}
