package mavmi.telegram_bot.monitoring.service.database.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RuleDto {
    private Long userid;
    private Boolean waterStuff;
    private Boolean monitoring;
}
