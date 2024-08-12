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
public class ConnectRq {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("version")
    private String version;
    @JsonProperty("support")
    private String[] support;
}
