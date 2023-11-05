package mavmi.telegram_bot.congrats.utils.database.model;

import lombok.*;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestModel {
    private Long id;
    private Long userId;
    private String message;
    private Date date;
    private Time time;
}
