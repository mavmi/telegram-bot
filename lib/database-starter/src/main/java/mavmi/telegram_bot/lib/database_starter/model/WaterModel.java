package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data", schema = "water_stuff_telegram_bot")
public class WaterModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "userid")
    private Long userId;
    @Column(name = "name")
    private String name;
    @Column(name = "days_diff")
    private Long daysDiff;
    @Column(name = "water_date")
    private Date waterDate;
    @Column(name = "fertilize_date")
    private Date fertilizeDate;
    @Column(name = "stop_notifications_until")
    private Long stopNotificationsUntil;
}
