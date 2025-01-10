package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons.*;

@Getter
@Setter
public class Buttons {
    @JsonProperty("common")
    private Common common;
    @JsonProperty("pms")
    private Pms pms;
    @JsonProperty("bot-access")
    private BotAccess botAccess;
    @JsonProperty("cert-generation")
    private CertGeneration certGeneration;
    @JsonProperty("privileges")
    private Privileges privileges;
    @JsonProperty("server-info")
    private ServerInfo serverInfo;
    @JsonProperty("apps")
    private Apps apps;
}
