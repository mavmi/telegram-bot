package mavmi.telegram_bot.common.database.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RocketImModel {
    private String rc_uid;
    private String chat_id;
    private String last_msg_id;
    private String last_msg_author_id;
}
