package mavmi.telegram_bot.rocketchat.service.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class CreateDMRq {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("method")
    private String method;
    @JsonProperty("id")
    private String id;
    @JsonProperty("params")
    private String[] params;
}
