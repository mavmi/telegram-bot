package mavmi.telegram_bot.rocketchat.webscoket.api.dto.inner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TelegramMessage {
    private String textMessage;
    private String filePath;
    private String[] replyKeyboard;
}
