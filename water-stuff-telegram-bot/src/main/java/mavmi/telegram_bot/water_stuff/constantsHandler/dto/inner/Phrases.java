package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.phrases.common.Common;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.phrases.manageGroup.ManageGroup;

@Getter
@Setter
public class Phrases {
    @JsonProperty("common")
    private Common common;
    @JsonProperty("manage-group")
    private ManageGroup manageGroup;
}
