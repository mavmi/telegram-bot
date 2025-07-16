package mavmi.telegram_bot.lib.metric_starter.service.database.dto;

import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
public class MetricDto {
    private String botName;
    private Long telegramId;
    private Date date;
    private Integer count;
    private Integer success;
    private Integer error;
}
