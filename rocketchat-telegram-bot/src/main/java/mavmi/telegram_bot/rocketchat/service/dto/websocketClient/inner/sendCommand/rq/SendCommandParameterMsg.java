package mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.sendCommand.rq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class SendCommandParameterMsg {
    @JsonProperty("_id")
    private String _id;
    @JsonProperty("rid")
    private String rid;
    @JsonProperty("msg")
    private String msg;
}
