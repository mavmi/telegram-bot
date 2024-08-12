package mavmi.telegram_bot.rocketchat.service.dto.websocketClient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
public class SubscribeForMsgUpdatesRq {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("id")
    private String id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("params")
    private List<Object> params;

    public static List<Object> createParams(String str, boolean bool) {
        return List.of(str, bool);
    }
}
