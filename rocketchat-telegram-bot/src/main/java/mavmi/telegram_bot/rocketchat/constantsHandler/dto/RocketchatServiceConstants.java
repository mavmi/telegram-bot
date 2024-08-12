package mavmi.telegram_bot.rocketchat.constantsHandler.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.constantsHandler.api.Constants;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Phrases;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.inner.Requests;

@Getter
@Setter
public class RocketchatServiceConstants implements Constants {
    private Requests requests;
    private Phrases phrases;
}
