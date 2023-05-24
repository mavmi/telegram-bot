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

    public String toInfoString(){
        return "\t" +
                "<<" +
                name +
                ">>" +
                "\n" +
                "Разница по дням: " +
                diff +
                "\n" +
                "Полив: " +
                ((water != null) ? water.toInfoString() : "null") +
                "\n" +
                "Удобрение: " +
                ((fertilize != null) ? fertilize.toInfoString() : "null");
    }
    public String toFileString(){
        return name +
                "\n" +
                diff +
                "\n" +
                ((water != null) ? water.toFileString() : "null") +
                "\n" +
                ((fertilize != null) ? fertilize.toFileString() : "null") +
                "\n";
    }

}
