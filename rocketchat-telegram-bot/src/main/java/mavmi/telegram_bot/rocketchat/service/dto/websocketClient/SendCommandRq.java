package mavmi.telegram_bot.rocketchat.service.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.sendCommand.rq.SendCommandParameter;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class SendCommandRq {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("method")
    private String method;
    @JsonProperty("id")
    private String id;
    @JsonProperty("params")
    private List<SendCommandParameter> params;
}
