package mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phrases {
    @JsonProperty("ok")
    private String ok;
    @JsonProperty("error")
    private String error;
    @JsonProperty("invalid_request")
    private String invalidRequest;
    @JsonProperty("unknown_command")
    private String unknownCommand;
    @JsonProperty("ask_for_rocketchat_creds")
    private String askForRocketchatCreds;
    @JsonProperty("creds_not_found")
    private String credsNotFound;
    @JsonProperty("invalid_creds")
    private String invalidCreds;
}