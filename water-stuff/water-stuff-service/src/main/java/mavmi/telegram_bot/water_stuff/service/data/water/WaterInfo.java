package mavmi.telegram_bot.water_stuff.service.data.water;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mavmi.telegram_bot.water_stuff.service.data.DataException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class WaterInfo {
    public static final String DATE_FORMAT_STR = "dd-MM-yyyy";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_STR);
    public static final String NULL_STR = "null";
    public static final String FILE_LINE_SEPARATOR = ";";

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("day_difference")
    private Integer diff;
    @JsonProperty("water_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_STR)
    private Date water;
    @JsonProperty("fertilize_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT_STR)
    private Date fertilize;
    @JsonProperty("stop_notifications_until")
    private Long stopNotificationsUntil;

    @JsonIgnore
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

    @JsonIgnore
    public String getWaterAsString() {
        return (water == null) ? NULL_STR : DATE_FORMAT.format(water);
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getFertilizeAsString() {
        return (fertilize == null) ? NULL_STR : DATE_FORMAT.format(fertilize);
    }
}
