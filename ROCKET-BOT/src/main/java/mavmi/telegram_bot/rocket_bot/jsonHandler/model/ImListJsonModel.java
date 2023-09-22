package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ImListJsonModel {
    private String chatId;
    private MessageJsonModel lastMessage;
    private List<ImHistoryJsonModel> historyResponses;
}
