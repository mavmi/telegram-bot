package mavmi.telegram_bot.common.service.dto.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Telegram bot service request
 */
@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public abstract class ServiceRequest {
    @JsonProperty("chat_id")
    protected Long chatId;
}
