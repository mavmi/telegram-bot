package mavmi.telegram_bot.common.dto.impl.water_stuff.telegram_bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRq;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.UserMessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffTelegramBotRq implements IRq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user_message_json")
    private UserMessageJson userMessageJson;
    @JsonProperty("keyboard")
    private KeyboardJson keyboardJson;
}
