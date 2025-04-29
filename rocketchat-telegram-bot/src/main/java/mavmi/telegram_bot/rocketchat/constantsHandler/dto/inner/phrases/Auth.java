package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Auth {
    @JsonProperty("auth_success")
    private String authSuccess;
    @JsonProperty("ask_for_rocketchat_creds")
    private String askForRocketchatCreds;
    @JsonProperty("creds_not_found")
    private String credsNotFound;
    @JsonProperty("invalid_creds")
    private String invalidCreds;
    @JsonProperty("enter_login")
    private String enterLogin;
    @JsonProperty("enter_password")
    private String enterPassword;
    @JsonProperty("already_logged_in")
    private String alreadyLoggedIn;
}
