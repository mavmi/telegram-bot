package mavmi.telegram_bot.common.utils.dto.json.bot;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotRequestJson {
    private Long chatId;
    private DiceJson diceJson;
    private UserJson userJson;
    private UserMessageJson userMessageJson;
}
