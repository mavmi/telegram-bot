package mavmi.telegram_bot.lib.menu_engine_starter.dto.configFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigFileButtonDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("value")
    private String value;
}
