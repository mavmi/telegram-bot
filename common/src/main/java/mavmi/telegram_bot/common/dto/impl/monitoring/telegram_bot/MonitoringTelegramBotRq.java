package mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRq;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringTelegramBotRq implements IRq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("chat_idx")
    private List<Long> chatIdx;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("file")
    private FileJson fileJson;
    @JsonProperty("keyboard")
    private KeyboardJson keyboardJson;
}
