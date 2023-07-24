package mavmi.telegram_bot.common.database.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserModel {
    private Long id;
    private Long chatId;
    private String username;
    private String firstName;
    private String lastName;
}
