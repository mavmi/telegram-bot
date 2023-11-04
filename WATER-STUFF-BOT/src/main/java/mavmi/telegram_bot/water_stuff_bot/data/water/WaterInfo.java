package mavmi.telegram_bot.water_stuff_bot.data.water;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import mavmi.telegram_bot.water_stuff_bot.data.DataException;
import org.springframework.lang.Nullable;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@NoArgsConstructor
public class WaterInfo {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final String NULL_STR = "null";
    public static final String FILE_LINE_SEPARATOR = ";";

    @Setter
    @Getter
    private Long userId;
    @Setter
    @Getter
    private String name;
    @Setter
    @Getter
    private Integer diff;
    private Date water;
    private Date fertilize;

    public static WaterInfo fromFileString(String line) {
        String[] splitted = line.split(FILE_LINE_SEPARATOR);

        if (splitted.length != 5) {
            throw new DataException("Invalid data line format");
        }

        try {
            WaterInfo waterInfo = new WaterInfo();

            waterInfo.setUserId(Long.parseLong(splitted[0]));
            waterInfo.setName(splitted[1]);
            waterInfo.setDiff(Integer.parseInt(splitted[2]));
            waterInfo.setWater(splitted[3]);
            waterInfo.setFertilize(splitted[4]);

            return waterInfo;
        } catch (NumberFormatException e) {
            throw new DataException(e);
        }
    }

    public String toFileString() {
        return getUserId() +
                FILE_LINE_SEPARATOR +
                getName() +
                FILE_LINE_SEPARATOR +
                getDiff() +
                FILE_LINE_SEPARATOR +
                getWaterAsString() +
                FILE_LINE_SEPARATOR +
                getFertilizeAsString();
    }

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

    @Nullable
    public Date getWaterAsDate() {
        return water;
    }

    @Nullable
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

    @Nullable
    public Date getFertilizeAsDate() {
        return fertilize;
    }

    @Nullable
    public String getFertilizeAsString() {
        return (fertilize == null) ? NULL_STR : DATE_FORMAT.format(fertilize);
    }
}
