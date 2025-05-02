package mavmi.telegram_bot.rocketchat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.metric_starter.mteric.api.Metric;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.rocketchat.timeout.aop.api.RequestsTimeout;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.AuthGetLoginServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.AuthGetPasswordServiceModule;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RocketService implements Service<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final MenuEngine menuEngine;

    @Override
    @RequestsTimeout
    @SetupUserCaches
    @Metric(BOT_NAME.ROCKETCHAT_BOT)
    public void handleRequest(RocketchatServiceRq request) {
        RocketDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(RocketDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson == null || messageJson.getTextMessage() == null) {
            commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getInvalidRequest());
            return;
        }

        Menu menu = dataCache.getMenuHistoryContainer().getLast();
        menuEngine.proxyRequest(menu, request);
    }
}
