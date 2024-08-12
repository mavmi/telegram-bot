package mavmi.telegram_bot.common.service.dto.common.inlineKeyboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InlineKeyboardButtonJson {
    private String key;
    private String value;
}
