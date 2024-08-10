package mavmi.telegram_bot.rocketchat.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.container.impl.MenuToServiceModuleContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.Service;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceAuthCache;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.mapper.RocketchatMapper;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;
import mavmi.telegram_bot.rocketchat.service.serviceModule.AuthServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Component
public class RocketchatService implements Service<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatMapper rocketchatMapper;
    private final RocketchatRepository rocketchatRepository;
    private final MenuToServiceModuleContainer<RocketchatServiceRs, RocketchatServiceRq> menuToServiceModuleContainer;
    private final CacheComponent cacheComponent;
    private final CommonServiceModule commonServiceModule;

    public RocketchatService(
            RocketchatMapper rocketchatMapper,
            RocketchatRepository rocketchatRepository,
            MainMenuServiceModule mainMenuServiceModule,
            AuthServiceModule authServiceModule,
            CacheComponent cacheComponent, CommonServiceModule commonServiceModule) {
        this.rocketchatMapper = rocketchatMapper;
        this.rocketchatRepository = rocketchatRepository;
        this.menuToServiceModuleContainer = new MenuToServiceModuleContainer<>(
                new HashMap<>() {{
                    put(RocketchatServiceMenu.MAIN_MENU, mainMenuServiceModule);
                    put(RocketchatServiceMenu.AUTH, authServiceModule);
                }}
        );
        this.cacheComponent = cacheComponent;
        this.commonServiceModule = commonServiceModule;
    }

    @SetupUserCaches
    @Secured
    @Override
    public RocketchatServiceRs handleRequest(RocketchatServiceRq request) {
        RocketchatServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(RocketchatServiceDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson == null) {
            return commonServiceModule.createBadRequestResponse();
        } else {
            Menu menu = dataCache.getMenuContainer().getLast();
            ServiceModule<RocketchatServiceRs, RocketchatServiceRq> module = menuToServiceModuleContainer.get(menu);
            return module.handleRequest(request);
        }
    }

    @Override
    public DataCache initDataCache(long chatId) {
        Optional<RocketchatModel> databaseRecord = rocketchatRepository.findById(chatId);
        if (databaseRecord.isEmpty()) {
            return new RocketchatServiceDataCache(chatId);
        } else {
            return rocketchatMapper.rocketchatDatabaseModelToRocketchatDataCache(databaseRecord.get());
        }
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new RocketchatServiceAuthCache(true);
    }
}
