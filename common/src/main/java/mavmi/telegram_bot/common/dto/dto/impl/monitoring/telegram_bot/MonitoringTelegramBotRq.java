package mavmi.telegram_bot.common.dto.dto.impl.monitoring.telegram_bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringTelegramBotRq implements Rq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("chat_idx")
    private List<Long> chatIdx;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("file")
    private FileJson fileJson;
    @JsonProperty("reply_keyboard")
    private ReplyKeyboardJson replyKeyboardJson;
}
