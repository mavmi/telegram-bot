package mavmi.telegram_bot.common.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeyboardJson {
    @JsonProperty("keyboard_buttons")
    private String[] keyboardButtons;
}
