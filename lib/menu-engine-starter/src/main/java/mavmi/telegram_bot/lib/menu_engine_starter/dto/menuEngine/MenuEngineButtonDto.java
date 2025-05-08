package mavmi.telegram_bot.lib.menu_engine_starter.dto.menuEngine;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuEngineButtonDto {
    private String name;
    private String value;
}
