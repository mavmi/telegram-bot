package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;

@Getter
@Builder
@AllArgsConstructor
public class ImHistoryJsonModel {
    private MessageJsonModel message;
}
