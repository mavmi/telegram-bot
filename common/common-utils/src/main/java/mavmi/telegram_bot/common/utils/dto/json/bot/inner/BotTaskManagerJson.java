package mavmi.telegram_bot.common.utils.dto.json.bot.inner;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotTaskManagerJson {
    private String target;
    private String message;
}
