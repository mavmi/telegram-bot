package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Privileges {
    @JsonProperty("info")
    private String info;
    @JsonProperty("add-privilege")
    private String addPrivilege;
    @JsonProperty("delete-privilege")
    private String deletePrivilege;
}
