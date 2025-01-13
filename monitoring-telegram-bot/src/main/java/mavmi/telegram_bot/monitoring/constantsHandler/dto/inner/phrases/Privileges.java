package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Privileges {
    @JsonProperty("ask-for-user-id")
    private String askForUserId;
    @JsonProperty("user-not-found")
    private String userNotFound;
    @JsonProperty("no-privileges")
    private String noPrivileges;
    @JsonProperty("invalid-id")
    private String invalidId;
    @JsonProperty("select-privilege")
    private String selectPrivilege;
}
