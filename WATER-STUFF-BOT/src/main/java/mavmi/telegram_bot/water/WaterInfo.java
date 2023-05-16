package mavmi.telegram_bot.water;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class WaterInfo {
    private String name;
    private Calen water;
    private Calen fertilize;
    private int diff;

    public WaterInfo(){

    }

    @Override
    public String toString() {
        return name + "\n" +
                diff + "\n" +
                water + "\n" +
                fertilize + "\n";
    }
}
