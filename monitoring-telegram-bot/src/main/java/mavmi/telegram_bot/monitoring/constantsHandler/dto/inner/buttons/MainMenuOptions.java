package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions.Apps;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions.BotAccess;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions.CertGeneration;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions.Pms;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions.Privileges;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.mainMenuOptions.ServerInfo;

@Getter
@Setter
public class MainMenuOptions {
    @JsonProperty("apps")
    private Apps apps;
    @JsonProperty("bot_access")
    private BotAccess botAccess;
    @JsonProperty("cert_generation")
    private CertGeneration certGeneration;
    @JsonProperty("pms")
    private Pms pms;
    @JsonProperty("privileges")
    private Privileges privileges;
    @JsonProperty("server_info")
    private ServerInfo serverInfo;
}
