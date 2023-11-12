package mavmi.telegram_bot.common.database.model;

import lombok.*;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestModel {
    private Long userid;
    private String message;
    private Date date;
    private Time time;
}
