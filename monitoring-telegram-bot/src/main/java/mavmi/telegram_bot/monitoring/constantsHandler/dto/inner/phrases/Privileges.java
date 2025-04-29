package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Privileges {
    @JsonProperty("ask_for_user_id")
    private String askForUserId;
    @JsonProperty("user_not_found")
    private String userNotFound;
    @JsonProperty("no_privileges")
    private String noPrivileges;
    @JsonProperty("invalid_id")
    private String invalidId;
    @JsonProperty("select_privilege")
    private String selectPrivilege;
}
