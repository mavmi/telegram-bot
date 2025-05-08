package mavmi.telegram_bot.lib.menu_engine_starter.mapper;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.dto.configFile.ConfigFileDto;
import mavmi.telegram_bot.lib.menu_engine_starter.dto.configFile.ConfigFileMenuDto;
import mavmi.telegram_bot.lib.menu_engine_starter.dto.menuEngine.MenuEngineButtonDto;
import mavmi.telegram_bot.lib.menu_engine_starter.dto.menuEngine.MenuEngineMenuDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigFileDtoMapper {

    public Map<Menu, MenuEngineMenuDto> map(ConfigFileDto configFileDto, List<Menu> menuList) {
        Map<String, MenuEngineMenuDto> menuNameToDto = configFileDto.getMenus()
                .stream()
                .flatMap(configFileMenuDto -> Stream.of(MenuEngineMenuDto.builder()
                        .name(configFileMenuDto.getName())
                        .menu(menuList.stream().filter(menu -> menu.getName().equals(configFileMenuDto.getName())).findFirst().orElseThrow())
                        .buttons(configFileMenuDto.getButtons()
                                .stream()
                                .flatMap(configFileButtonDto -> Stream.of(MenuEngineButtonDto.builder()
                                                .name(configFileButtonDto.getName())
                                                .value(configFileButtonDto.getValue())
                                        .build()))
                                .toList()
                        )
                        .submenus(new ArrayList<>())
                        .build()))
                .collect(Collectors.toMap(MenuEngineMenuDto::getName, menuEngineMenuDto -> menuEngineMenuDto));

        for (ConfigFileMenuDto configFileMenuDto : configFileDto.getMenus()) {
            String name = configFileMenuDto.getName();
            MenuEngineMenuDto menuEngineMenuDto = menuNameToDto.get(name);

            menuEngineMenuDto.getSubmenus()
                    .addAll(configFileMenuDto.getSubmenus()
                            .stream()
                            .flatMap(submenuName -> Stream.of(menuNameToDto.get(submenuName)))
                            .toList());
        }

        return menuNameToDto.entrySet()
                .stream()
                .collect(Collectors.toMap(entry -> entry.getValue().getMenu(), Map.Entry::getValue));
    }
}
