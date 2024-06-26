package mavmi.telegram_bot.common.dto.dto.impl.shakal.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShakalServiceRq implements Rq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user")
    private UserJson userJson;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("dice")
    private DiceJson diceJson;
}
