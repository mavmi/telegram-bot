package mavmi.telegram_bot.common.service.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageJson {
    @JsonProperty("text_message")
    private String textMessage;
    @JsonProperty("date")
    private Date date;
}
