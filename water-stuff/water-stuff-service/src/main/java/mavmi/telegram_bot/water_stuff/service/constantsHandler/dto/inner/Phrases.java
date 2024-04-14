package mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phrases {
    @JsonProperty("manage-group")
    private String manageGroup;
    private String success;
    private String error;
    @JsonProperty("on-empty")
    private String onEmpty;
    private String approve;
    private String add;
    @JsonProperty("enter-group-name")
    private String enterGroupName;
    @JsonProperty("enter-group-data")
    private String enterGroupData;
    @JsonProperty("invalid-group-name-format")
    private String invalidGroupNameFormat;
    @JsonProperty("invalid-group-name")
    private String invalidGroupName;
    @JsonProperty("operation-canceled")
    private String operationCanceled;
}
