package mavmi.telegram_bot.lib.dto.service.common.inlineKeyboard;

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
