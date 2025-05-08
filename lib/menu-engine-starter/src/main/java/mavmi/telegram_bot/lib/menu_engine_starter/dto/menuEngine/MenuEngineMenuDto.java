package mavmi.telegram_bot.lib.menu_engine_starter.dto.menuEngine;

import lombok.Builder;
import lombok.Getter;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;

import java.util.List;

@Getter
@Builder
public class MenuEngineMenuDto {
    private String name;
    private Menu menu;
    private List<MenuEngineMenuDto> submenus;
    private List<MenuEngineButtonDto> buttons;
}
