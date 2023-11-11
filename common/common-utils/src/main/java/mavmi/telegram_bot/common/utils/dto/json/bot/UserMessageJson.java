package mavmi.telegram_bot.common.utils.dto.json.bot;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMessageJson {
    private String textMessage;
    private Date date;
}
