package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BotAccess {
    @JsonProperty("info")
    private String info;
    @JsonProperty("add_water_stuff")
    private String addWaterStuff;
    @JsonProperty("revoke_water_stuff")
    private String revokeWaterStuff;
    @JsonProperty("add_monitoring")
    private String addMonitoring;
    @JsonProperty("revoke_monitoring")
    private String revokeMonitoring;
}
