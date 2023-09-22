package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MessageJsonModel {
    private String id;
    private String text;
    private String timestamp;
    private UserJsonModel author;
}
