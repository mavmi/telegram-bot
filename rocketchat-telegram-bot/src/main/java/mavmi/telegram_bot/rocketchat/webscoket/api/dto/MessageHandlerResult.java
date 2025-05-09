package mavmi.telegram_bot.rocketchat.webscoket.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import mavmi.telegram_bot.rocketchat.webscoket.api.dto.inner.TelegramMessage;

@Getter
@Builder
@AllArgsConstructor
public class MessageHandlerResult {
    private boolean runNext;
    private boolean sendTelegramMessage;
    private String websocketMessage;
    private TelegramMessage telegramMessage;
}
