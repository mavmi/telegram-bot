package mavmi.telegram_bot.common.dto.json.bot.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageJson {
    @JsonProperty("text_message")
    private String textMessage;
    @JsonProperty("date")
    private Date date;
}
