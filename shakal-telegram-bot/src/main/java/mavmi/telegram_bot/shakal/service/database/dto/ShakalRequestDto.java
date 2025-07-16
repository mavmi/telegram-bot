package mavmi.telegram_bot.shakal.service.database.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Builder
public class ShakalRequestDto {
    private Long id;
    private Long userid;
    private String message;
    private Date date;
    private Time time;
}
