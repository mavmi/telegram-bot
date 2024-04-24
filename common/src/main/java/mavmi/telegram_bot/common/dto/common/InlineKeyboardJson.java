package mavmi.telegram_bot.common.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.common.inlineKeyboard.InlineKeyboardRowJson;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InlineKeyboardJson {
    @JsonProperty("keyboard_buttons")
    private List<InlineKeyboardRowJson> keyboardButtons;
}
