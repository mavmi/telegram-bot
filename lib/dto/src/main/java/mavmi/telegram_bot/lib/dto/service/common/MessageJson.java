package mavmi.telegram_bot.lib.dto.service.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageJson {
    @JsonProperty("msg_id")
    private Integer msgId;
    @JsonProperty("text_message")
    private String textMessage;
    @JsonProperty("date")
    private Date date;
}
