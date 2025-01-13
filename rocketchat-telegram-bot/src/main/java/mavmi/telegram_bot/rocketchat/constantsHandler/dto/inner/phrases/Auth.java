package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.phrases;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Auth {
    @JsonProperty("auth-success")
    private String authSuccess;
    @JsonProperty("ask-for-rocketchat-creds")
    private String askForRocketchatCreds;
    @JsonProperty("creds-not-found")
    private String credsNotFound;
    @JsonProperty("invalid-creds")
    private String invalidCreds;
    @JsonProperty("enter-login")
    private String enterLogin;
    @JsonProperty("enter-password")
    private String enterPassword;
    @JsonProperty("already-logged-in")
    private String alreadyLoggedIn;
}
