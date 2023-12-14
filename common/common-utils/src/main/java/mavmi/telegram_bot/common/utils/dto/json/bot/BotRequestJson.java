package mavmi.telegram_bot.common.utils.dto.json.bot;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("dice_json")
    private DiceJson diceJson;
    @JsonProperty("user_json")
    private UserJson userJson;
    @JsonProperty("user_message_json")
    private UserMessageJson userMessageJson;
    @JsonProperty("bot_task_manager_json")
    private BotTaskManagerJson botTaskManagerJson;
}
