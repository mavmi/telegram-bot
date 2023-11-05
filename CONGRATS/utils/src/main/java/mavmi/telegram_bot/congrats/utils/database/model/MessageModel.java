package mavmi.telegram_bot.congrats.utils.database.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageModel {
    private Long id;
    private String message;
}
