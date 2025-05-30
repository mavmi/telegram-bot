package mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.createDm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Result {
    @JsonProperty("t")
    private String t;
    @JsonProperty("rid")
    private String rid;
    @JsonProperty("usernames")
    private String[] usernames;
}
