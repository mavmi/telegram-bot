package mavmi.telegram_bot.monitoring.service.constantsHandler.dto.inner;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Buttons {
    private String pk;
    private String fp;
    private String gc;
    @JsonProperty("memory-info")
    private String memoryInfo;
    @JsonProperty("ram-info")
    private String ramInfo;
    @JsonProperty("users-info")
    private String usersInfo;
    private String backup;
    private String exit;
}
