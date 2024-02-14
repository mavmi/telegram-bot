package mavmi.telegram_bot.common.dto.impl.water_stuff.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRq;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.UserMessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffServiceDtoRq implements IRq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user_json")
    private UserJson userJson;
    @JsonProperty("user_message_json")
    private UserMessageJson userMessageJson;
}
