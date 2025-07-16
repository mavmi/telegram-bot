package mavmi.telegram_bot.water_stuff.service.database.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Getter
@Setter
@Builder
@Accessors(chain = true)
public class WaterStuffDto {

    public static final String DATE_FORMAT_STR = "dd-MM-yyyy";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
    public static final String NULL_STR = "null";

    private Long id;
    private Long userId;
    private String name;
    private Long daysDiff;
    private Date waterDate;
    private Date fertilizeDate;
    private Long stopNotificationsUntil;

    public String getWaterAsString() {
        return (waterDate == null) ? null : DATE_FORMAT.format(waterDate);
    }

    public String getFertilizeAsString() {
        return (fertilizeDate == null) ? null : DATE_FORMAT.format(fertilizeDate);
    }

    public WaterStuffDto setWaterFromString(String water) {
        if (water.equals(NULL_STR)) {
            this.waterDate = null;
        } else {
            try {
                this.waterDate = new Date(DATE_FORMAT.parse(water).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }

    public WaterStuffDto setFertilizeFromString(String fertilize) {
        if (fertilize.equals(NULL_STR)) {
            this.fertilizeDate = null;
        } else {
            try {
                this.fertilizeDate = new Date(DATE_FORMAT.parse(fertilize).getTime());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

        return this;
    }
}
