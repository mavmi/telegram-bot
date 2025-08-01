package mavmi.telegram_bot.rocketchat.service.rocketchat.dto.websocketClient.inner.sendCommand.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SendCommandParameter {
    @JsonProperty("cmd")
    private String cmd;
    @JsonProperty("params")
    private String params;
    @JsonProperty("msg")
    private SendCommandParameterMsg msg;
    @JsonProperty("triggerId")
    private String triggerId;
}
