package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.sql.Time;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "request")
public class RequestModel {
    @Id
    @Column(name = "id", columnDefinition = "serial")
    private Long id;
    @Column(name = "userid")
    private Long userid;
    @Column(name = "message")
    private String message;
    @Column(name = "date")
    private Date date;
    @Column(name = "time")
    private Time time;
}
