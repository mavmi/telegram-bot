package mavmi.telegram_bot.rocketchat.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.metric.api.Metric;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.service.Service;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.aop.timeout.api.RequestsTimeout;
import mavmi.telegram_bot.rocketchat.cache.RocketAuthCache;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.AuthGetLoginServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.AuthGetPasswordServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RocketService implements Service<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public RocketService(
            MainMenuServiceModule mainMenuServiceModule,
            AuthGetLoginServiceModule authGetLoginServiceModule,
            AuthGetPasswordServiceModule authGetPasswordServiceModule,
            CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(RocketMenu.MAIN_MENU, mainMenuServiceModule)
                .add(RocketMenu.AUTH_ENTER_LOGIN, authGetLoginServiceModule)
                .add(RocketMenu.AUTH_ENTER_PASSWORD, authGetPasswordServiceModule)
                .add(RocketMenu.AUTH_ENTER_PASSWORD, authGetPasswordServiceModule);
    }

    @Override
    @RequestsTimeout
    @SetupUserCaches
    @Metric(BOT_NAME.ROCKETCHAT_BOT)
    public void handleRequest(RocketchatServiceRq request) {
        RocketDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson == null || messageJson.getTextMessage() == null) {
            commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getInvalidRequest());
        } else {
            Menu menu = dataCache.getMenuContainer().getLast();
            ServiceModule<RocketchatServiceRq> module = serviceComponentsContainer.getModule(menu);
            module.handleRequest(request);
        }
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return new RocketDataCache(chatId);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new RocketAuthCache(true);
    }
}
