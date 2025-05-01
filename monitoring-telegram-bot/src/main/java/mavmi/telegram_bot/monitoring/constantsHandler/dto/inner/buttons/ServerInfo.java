package mavmi.telegram_bot.monitoring.constantsHandler.dto.inner.buttons;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServerInfo {
    @JsonProperty("memory_info")
    private String memoryInfo;
    @JsonProperty("ram_info")
    private String ramInfo;
    @JsonProperty("users_info")
    private String usersInfo;
    @JsonProperty("backup")
    private String backup;
}
