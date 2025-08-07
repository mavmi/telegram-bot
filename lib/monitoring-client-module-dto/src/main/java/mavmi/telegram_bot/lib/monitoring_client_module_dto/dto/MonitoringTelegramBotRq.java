package mavmi.telegram_bot.lib.monitoring_client_module_dto.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringTelegramBotRq {
    @JsonProperty("from")
    private String from;
    @JsonProperty("message")
    private String message;
}
