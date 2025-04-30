package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.manageGroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.manageGroup.editGroup.EditGroup;

@Getter
@Setter
public class ManageGroup {
    @JsonProperty("info")
    private String info;
    @JsonProperty("pause")
    private String pause;
    @JsonProperty("continue")
    private String doContinue;
    @JsonProperty("water")
    private String water;
    @JsonProperty("fertilize")
    private String fertilize;
    @JsonProperty("edit")
    private String edit;
    @JsonProperty("rm")
    private String rm;
    @JsonProperty("edit_group")
    private EditGroup editGroup;
}
