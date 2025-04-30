package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.manageGroup.editGroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditGroup {
    @JsonProperty("change_name")
    private String changeName;
    @JsonProperty("change_diff")
    private String changeDiff;
    @JsonProperty("change_water")
    private String changeWater;
    @JsonProperty("change_fertilize")
    private String changeFertilize;
}
