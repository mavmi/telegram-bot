package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.common.Common;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.manageGroup.ManageGroup;

@Getter
@Setter
public class Buttons {
    @JsonProperty("common")
    private Common common;
    @JsonProperty("manage-group")
    private ManageGroup manageGroup;
}
