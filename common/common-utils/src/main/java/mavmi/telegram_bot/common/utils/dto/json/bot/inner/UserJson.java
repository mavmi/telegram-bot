package mavmi.telegram_bot.common.utils.dto.json.bot.inner;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserJson {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
}
