package mavmi.telegram_bot.common.dto.impl.shakal.telegram_bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRq;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShakalTelegramBotRq implements IRq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user_message_json")
    private MessageJson messageJson;
    @JsonProperty("keyboard_json")
    private KeyboardJson keyboardJson;
    @JsonProperty("dice_json")
    private DiceJson diceJson;
}
