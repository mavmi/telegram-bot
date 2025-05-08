package mavmi.telegram_bot.water_stuff.service.waterStuff;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.water_stuff.cache.dto.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CancelRequestServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WaterService implements Service<WaterStuffServiceRq> {

    private final CommonUtils commonUtils;
    private final MenuEngine menuEngine;
    private final CancelRequestServiceModule cancelRequestServiceModule;

    private WaterConstants constants;

    @Autowired
    public void setup(WaterConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    @SetupUserCaches
    @Secured
    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonUtils.getUserCaches().getDataCache(WaterDataCache.class);
        MessageJson messageJson = request.getMessageJson();

        log.info("Got request from id: {}", dataCache.getUserId());

        if (messageJson != null && constants.getRequests().getCancel().equals(request.getMessageJson().getTextMessage())) {
            cancelRequestServiceModule.handleRequest(request);
        } else {
            Menu menu = commonUtils.getUserCaches().getDataCache(WaterDataCache.class).getMenuHistoryContainer().getLast();
            menuEngine.proxyRequest(menu, request);
        }
    }
}
