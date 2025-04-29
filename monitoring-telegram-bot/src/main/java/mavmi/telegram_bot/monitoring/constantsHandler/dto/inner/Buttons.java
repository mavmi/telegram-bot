package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.*;

@Getter
@Setter
public class Buttons {
    @JsonProperty("main_menu_options")
    private MainMenuOptions mainMenuOptions;
    @JsonProperty("common")
    private Common common;
    @JsonProperty("pms")
    private Pms pms;
    @JsonProperty("bot_access")
    private BotAccess botAccess;
    @JsonProperty("cert_generation")
    private CertGeneration certGeneration;
    @JsonProperty("privileges")
    private Privileges privileges;
    @JsonProperty("server_info")
    private ServerInfo serverInfo;
    @JsonProperty("apps")
    private Apps apps;
}
