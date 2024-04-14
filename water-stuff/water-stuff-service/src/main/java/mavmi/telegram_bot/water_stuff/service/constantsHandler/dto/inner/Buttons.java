package mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Buttons {
    private String yes;
    private String no;
    private String info;
    private String pause;
    @JsonProperty("continue")
    private String doContinue;
    private String water;
    private String fertilize;
    private String edit;
    private String rm;
    private String exit;
}
