package mavmi.telegram_bot.common.utils.dto.json.bot;

import lombok.*;
import mavmi.telegram_bot.common.utils.dto.json.IRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.DiceJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.UserJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.UserMessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BotRequestJson implements IRequestJson {
    private Long chatId;
    private DiceJson diceJson;
    private UserJson userJson;
    private UserMessageJson userMessageJson;
    private BotTaskManagerJson botTaskManagerJson;
}
