package mavmi.telegram_bot.lib.menu_engine_starter.dto.configFile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigFileMenuDto {
    @JsonProperty("name")
    private String name;
    @JsonProperty("submenus")
    private List<String> submenus;
    @JsonProperty("buttons")
    private List<ConfigFileButtonDto> buttons;
}
