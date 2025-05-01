package mavmi.telegram_bot.lib.menu_engine_starter.dto.menuEngine;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MenuEngineMenuDto {
    private String name;
    private List<MenuEngineMenuDto> submenus;
    private List<String> buttons;
}
