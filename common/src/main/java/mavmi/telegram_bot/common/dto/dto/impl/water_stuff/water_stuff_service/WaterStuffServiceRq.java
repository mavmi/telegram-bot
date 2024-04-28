package mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.common.CallbackQueryJson;
import mavmi.telegram_bot.common.dto.dto.api.Rq;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WaterStuffServiceRq implements Rq {
    @JsonProperty("chat_id")
    private Long chatId;
    @JsonProperty("user")
    private UserJson userJson;
    @JsonProperty("message")
    private MessageJson messageJson;
    @JsonProperty("callback_query")
    private CallbackQueryJson callbackQueryJson;
}
