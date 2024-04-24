package mavmi.telegram_bot.common.dto.dto.impl.water_stuff.telegram_bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffTelegramBotRq implements Rq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("reply_keyboard")
    private ReplyKeyboardJson replyKeyboardJson;
}
