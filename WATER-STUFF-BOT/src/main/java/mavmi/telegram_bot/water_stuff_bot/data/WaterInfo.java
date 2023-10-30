package mavmi.telegram_bot.water_stuff_bot.data;

import lombok.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
public class WaterInfo {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String NULL_STR = "null";

    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Integer diff;
    private Date water;
    private Date fertilize;

    public void setWater(Date water) {
        this.water = water;
    }

    public void setWater(String water) {
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

    public Date getWaterAsDate() {
        return water;
    }

    public String getWaterAsString() {
        return (water == null) ? NULL_STR : DATE_FORMAT.format(water);
    }

    public void setFertilize(Date fertilize) {
        this.fertilize = fertilize;
    }

    public void setFertilize(String fertilize) {
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

    public Date getFertilizeAsDate() {
        return fertilize;
    }

    public String getFertilizeAsString() {
        return (fertilize == null) ? NULL_STR : DATE_FORMAT.format(fertilize);
    }
}
