package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
public class GroupsHistoryJsonModel {
    private MessageJsonModel message;
    private Set<String> mentionsIdx;
}
