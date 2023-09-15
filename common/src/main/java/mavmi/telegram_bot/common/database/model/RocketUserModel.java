package mavmi.telegram_bot.common.database.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RocketUserModel {
    private Long userid;
    private String login;
    private String passwd;
    private String rc_uid;
    private String rc_token;
    private Long token_exp;
}
