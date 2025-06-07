package mavmi.telegram_bot.water_stuff.data.water.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mavmi.telegram_bot.water_stuff.data.exception.DataException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Represents single data unit
 */
@Getter
@Setter
@NoArgsConstructor
public class WaterInfo {

    public static final String DATE_FORMAT_STR = "dd-MM-yyyy";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
    public static final String NULL_STR = "null";

    private Long userId;
    private String name;
    private Integer diff;
    private Date water;
    private Date fertilize;
    private Long stopNotificationsUntil;

    public void setWaterFromString(String water) {
        if (water.equals(NULL_STR)) {
            this.water = null;
        } else {
            try {
                this.water = DATE_FORMAT.parse(water);
            } catch (ParseException e) {
                throw new DataException(e);
            }
        }
    }

    public String getWaterAsString() {
        return (water == null) ? NULL_STR : DATE_FORMAT.format(water);
    }

    public void setFertilizeFromString(String fertilize) {
        if (fertilize.equals(NULL_STR)) {
            this.fertilize = null;
        } else {
            try {
                this.fertilize = DATE_FORMAT.parse(fertilize);
            } catch (ParseException e) {
                throw new DataException(e);
            }
        }

    }

    public String getFertilizeAsString() {
        return (fertilize == null) ? NULL_STR : DATE_FORMAT.format(fertilize);
    }
}
