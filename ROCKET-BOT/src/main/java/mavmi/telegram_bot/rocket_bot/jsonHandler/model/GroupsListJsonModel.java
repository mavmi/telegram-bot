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
public class GroupsListJsonModel {
    private String groupId;
    private String groupName;
    private MessageJsonModel lastMessage;
    private List<GroupsHistoryJsonModel> historyResponses;
}
