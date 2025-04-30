package mavmi.telegram_bot.water_stuff.constantsHandler.dto.inner.phrases.manageGroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManageGroup {
    @JsonProperty("manage_group")
    private String manageGroup;
    @JsonProperty("edit_group")
    private String editGroup;
    @JsonProperty("on_empty")
    private String onEmpty;
    private String add;
    @JsonProperty("enter_group_name")
    private String enterGroupName;
    @JsonProperty("enter_group_diff")
    private String enterGroupDiff;
    @JsonProperty("enter_group_data")
    private String enterGroupData;
    @JsonProperty("invalid_group_name_format")
    private String invalidGroupNameFormat;
    @JsonProperty("invalid_group_name")
    private String invalidGroupName;
    @JsonProperty("invalid_date")
    private String invalidDate;
}
