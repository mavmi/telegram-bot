package mavmi.telegram_bot.lib.dto.service.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CallbackQueryJson {
    @JsonProperty("message_id")
    private Integer messageId;
    private String data;
}
