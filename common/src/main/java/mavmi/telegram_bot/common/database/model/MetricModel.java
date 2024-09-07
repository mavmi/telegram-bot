package mavmi.telegram_bot.common.database.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.sql.Date;

@Getter
@Setter
@Builder
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "metric", schema = "common")
public class MetricModel {
    @Id
    @Column(name = "id", columnDefinition = "serial")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "bot_name")
    private String botName;
    @Column(name = "telegram_id")
    private Long telegramId;
    @Column(name = "date")
    private Date date;
    @Column(name = "count")
    private Integer count;
    @Column(name = "success")
    private Integer success;
    @Column(name = "error")
    private Integer error;
}
