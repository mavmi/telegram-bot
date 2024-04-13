package mavmi.telegram_bot.common.dto.dto.impl.monitoring.async_tasc_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.dto.api.Rs;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskManagerRs implements Rs {
    @JsonProperty("initiator_id")
    private Long initiatorId;
    @JsonProperty("message")
    private String message;
}
