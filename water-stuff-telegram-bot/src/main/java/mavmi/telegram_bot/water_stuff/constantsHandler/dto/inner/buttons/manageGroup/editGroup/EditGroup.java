package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.buttons.manageGroup.editGroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditGroup {
    @JsonProperty("change-name")
    private String changeName;
    @JsonProperty("change-diff")
    private String changeDiff;
    @JsonProperty("change-water")
    private String changeWater;
    @JsonProperty("change-fertilize")
    private String changeFertilize;
}
