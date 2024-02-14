package mavmi.telegram_bot.common.dto.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiceJson {
    @JsonProperty("bot_dice_value")
    private Integer botDiceValue;
    @JsonProperty("user_dice_value")
    private Integer userDiceValue;
}
