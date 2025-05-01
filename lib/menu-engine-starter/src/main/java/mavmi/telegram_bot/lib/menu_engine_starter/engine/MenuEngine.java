package mavmi.telegram_bot.lib.menu_engine_starter.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;
import mavmi.telegram_bot.lib.menu_engine_starter.dto.configFile.ConfigFileDto;
import mavmi.telegram_bot.lib.menu_engine_starter.dto.menuEngine.MenuEngineMenuDto;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.exception.MenuEngineException;
import mavmi.telegram_bot.lib.menu_engine_starter.handler.api.MenuRequestHandler;
import mavmi.telegram_bot.lib.menu_engine_starter.mapper.ConfigFileDtoMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MenuEngine {

    private final Map<Menu, MenuRequestHandler> menuToHandler = new HashMap<>();

    private List<MenuEngineMenuDto> menuEngineMenuDtoList;

    @Value("classpath:menu_engine/config.json")
    private Resource configFileResource;

    @PostConstruct
    public void setup() {
        this.menuEngineMenuDtoList = mapConfigFile();
    }

    public void registerHandler(MenuRequestHandler handler) {
        Menu menu = handler.getMenu();
        if (menuToHandler.containsKey(menu)) {
            throw new MenuEngineException("Handler with menu " + menu + " already exists");
        }

        menuToHandler.put(menu, handler);
    }

    public void proxyRequest(Menu menu, ServiceRequest request) {
        MenuRequestHandler handler = menuToHandler.get(menu);
        if (handler == null) {
            throw new MenuEngineException("Didn't find request handler for menu " + menu);
        }

        handler.handleRequest(request);
    }

    private List<MenuEngineMenuDto> mapConfigFile() {
        return new ConfigFileDtoMapper().map(readConfigFile());
    }

    @SneakyThrows
    private ConfigFileDto readConfigFile() {
        return new ObjectMapper().readValue(configFileResource.getInputStream(), ConfigFileDto.class);
    }

}
