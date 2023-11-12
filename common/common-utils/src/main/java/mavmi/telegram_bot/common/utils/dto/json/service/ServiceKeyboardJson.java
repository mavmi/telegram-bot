package mavmi.telegram_bot.common.utils.dto.json.service;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceKeyboardJson {
    private String[] keyboardButtons;
}
