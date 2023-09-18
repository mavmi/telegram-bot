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
public class ImListResponse {
    private String rc_uid;
    private String chat_id;
    private String last_msg_id;
    private String last_msg_author_id;
    private String last_msg_author_name;
    private List<ImHistoryResponse> historyResponses;
}
