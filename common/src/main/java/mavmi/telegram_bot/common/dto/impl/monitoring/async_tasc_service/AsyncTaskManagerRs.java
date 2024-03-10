package mavmi.telegram_bot.common.dto.impl.monitoring.async_tasc_service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import mavmi.telegram_bot.common.dto.api.IRs;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsyncTaskManagerRs implements IRs {
    @JsonProperty("initiator_id")
    private Long initiatorId;
    @JsonProperty("message")
    private String message;
}
