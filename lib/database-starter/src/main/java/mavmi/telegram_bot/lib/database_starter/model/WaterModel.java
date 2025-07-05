package mavmi.telegram_bot.lib.database_starter.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "data", schema = "water_stuff_telegram_bot")
public class WaterModel {

    public static final String DATE_FORMAT_STR = "dd-MM-yyyy";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
    public static final String NULL_STR = "null";

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

    public String getWaterAsString() {
        return (waterDate == null) ? null : DATE_FORMAT.format(waterDate);
    }

    public String getFertilizeAsString() {
        return (fertilizeDate == null) ? null : DATE_FORMAT.format(fertilizeDate);
    }

    public void setWaterFromString(String water) {
        if (water.equals(NULL_STR)) {
            this.waterDate = null;
        } else {
            try {
                this.waterDate = new Date(DATE_FORMAT.parse(water).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setFertilizeFromString(String fertilize) {
        if (fertilize.equals(NULL_STR)) {
            this.fertilizeDate = null;
        } else {
            try {
                this.fertilizeDate = new Date(DATE_FORMAT.parse(fertilize).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
