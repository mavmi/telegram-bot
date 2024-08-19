package mavmi.telegram_bot.hb.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Phrases {
    @JsonProperty("ok")
    private String ok;
    @JsonProperty("ask_for_peer_username")
    private String askForPeerUsername;
    @JsonProperty("ask_for_event_group_name")
    private String askForEventGroupName;
    @JsonProperty("ask_for_event_name")
    private String askForEventName;
    @JsonProperty("ask_for_grade")
    private String askForGrade;
    @JsonProperty("ask_for_prise_id")
    private String askForPriseId;
    @JsonProperty("invalid_peer_username")
    private String invalidPeerUsername;
    @JsonProperty("invalid_event_group_name")
    private String invalidEventGroupName;
    @JsonProperty("invalid_event_name")
    private String invalidEventName;
    @JsonProperty("invalid_request")
    private String invalidRequest;
    @JsonProperty("invalid_grade")
    private String invalidGrade;
    @JsonProperty("auth_success")
    private String authSuccess;
    @JsonProperty("save_peer_data_success")
    private String savePeerDataSuccess;
    @JsonProperty("fortune_count")
    private String fortuneCount;
    @JsonProperty("ask_for_password")
    private String askForPassword;
    @JsonProperty("invalid_password")
    private String invalidPassword;
    @JsonProperty("already_logged_in")
    private String alreadyLoggedIn;
    @JsonProperty("not_authorized")
    private String notAuthorized;
    @JsonProperty("not_enough_coins")
    private String notEnoughCoins;
    @JsonProperty("cancel")
    private String cancel;
}
