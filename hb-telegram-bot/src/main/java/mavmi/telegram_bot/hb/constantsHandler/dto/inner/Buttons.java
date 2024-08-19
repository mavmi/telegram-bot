package mavmi.telegram_bot.hb.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Buttons {
    @JsonProperty("grade")
    private String[] grade;
    @JsonProperty("menu_back")
    private String menuBack;
}
