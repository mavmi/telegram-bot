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
public class SendCommandRq {
    @JsonProperty("command")
    private String command;
    @JsonProperty("roomId")
    private String roomId;
}
