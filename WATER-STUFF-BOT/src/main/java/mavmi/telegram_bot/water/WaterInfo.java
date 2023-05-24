package mavmi.telegram_bot.water;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class WaterInfo implements Water {
    private String name;
    private Calen water;
    private Calen fertilize;
    private int diff;

    public WaterInfo(){

    }

    @Override
    public String toInfoString(){
        return toInfoString(true);
    }

    @Override
    public String toInfoString(boolean markdown){
        StringBuilder builder = new StringBuilder();

        if (markdown) builder.append("***");
        builder.append("> ")
                .append(name);
        if (markdown) builder.append("***");
        builder.append("\n")
                .append("Разница по дням: ")
                .append(diff)
                .append("\n")
                .append("Полив: ")
                .append(((water != null) ? water.toInfoString(markdown) : "null"))
                .append("\n")
                .append("Удобрение: ")
                .append(((water != null) ? fertilize.toInfoString(markdown) : "null"));

        return builder.toString();
    }

    @Override
    public String toFileString(){
        return new StringBuilder()
                .append(name)
                .append("\n")
                .append(diff)
                .append("\n")
                .append(((water != null) ? water.toFileString() : "null"))
                .append("\n")
                .append(((fertilize != null) ? fertilize.toFileString() : "null"))
                .append("\n")
                .toString();
    }

}
