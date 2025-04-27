package mavmi.telegram_bot.lib.dto.service.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeleteMessageJson {
    @JsonProperty("msg_id")
    private Integer msgId;
    @JsonProperty("delete_before_next")
    private Boolean deleteBeforeNext;
    @JsonProperty("delete_after_millis")
    private Long deleteAfterMillis;
}
