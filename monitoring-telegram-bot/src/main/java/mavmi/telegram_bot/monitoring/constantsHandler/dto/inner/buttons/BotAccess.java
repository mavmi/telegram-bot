package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotAccess {
    @JsonProperty("info")
    private String info;
    @JsonProperty("add-water-stuff")
    private String addWaterStuff;
    @JsonProperty("revoke-water-stuff")
    private String revokeWaterStuff;
    @JsonProperty("add-monitoring")
    private String addMonitoring;
    @JsonProperty("revoke-monitoring")
    private String revokeMonitoring;
}
