package mavmi.telegram_bot.common.utils.dto.json.bot;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiceJson {
    private Integer botDiceValue;
    private Integer userDiceValue;
}
