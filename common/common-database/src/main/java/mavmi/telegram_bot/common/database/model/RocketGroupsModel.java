package mavmi.telegram_bot.common.database.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RocketGroupsModel {
    private String rc_uid;
    private String group_id;
    private String last_msg_id;
    private String last_msg_author_id;
}
