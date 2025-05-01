package mavmi.telegram_bot.lib.dto.service.common.inlineKeyboard;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InlineKeyboardRowJson {
    private List<InlineKeyboardButtonJson> row;
}
