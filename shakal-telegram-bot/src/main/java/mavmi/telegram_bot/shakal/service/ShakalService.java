package mavmi.telegram_bot.shakal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.model.RequestModel;
import mavmi.telegram_bot.lib.database_starter.model.UserModel;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.UserJson;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.shakal.cache.dto.ShakalDataCache;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menuHandlers.utils.CommonUtils;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;

/**
 * Shakal bot service entrypoint
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ShakalService implements Service<ShakalServiceRq> {

    private final MenuEngine menuEngine;
    private final CommonUtils commonUtils;

    @SetupUserCaches
    @Secured
    @Override
    public void handleRequest(ShakalServiceRq request) {
        updateDatabase(request);

        String msg = null;
        UserJson userJson = request.getUserJson();
        ShakalDataCache dataCache = commonUtils.getUserCaches().getDataCache(ShakalDataCache.class);
        Menu menu = dataCache.getMenuHistoryContainer().getLast();
        if (userJson != null) {
            msg = request.getMessageJson().getTextMessage();
        }

        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                request.getChatId(),
                request.getUserJson().getUsername(),
                request.getUserJson().getFirstName(),
                request.getUserJson().getLastName(),
                msg
        );

        menuEngine.proxyRequest(menu, request);
    }

    private void updateDatabase(ShakalServiceRq shakalServiceRq) {
        UserJson userJson = shakalServiceRq.getUserJson();
        MessageJson messageJson = shakalServiceRq.getMessageJson();

        if (userJson != null) {
            commonUtils.getUserRepository().save(
                    new UserModel(
                            userJson.getId(),
                            shakalServiceRq.getChatId(),
                            userJson.getUsername(),
                            userJson.getFirstName(),
                            userJson.getLastName()
                    )
            );
        }

        if (messageJson != null) {
            commonUtils.getRequestRepository().save(
                    new RequestModel(
                            null,
                            userJson.getId(),
                            messageJson.getTextMessage(),
                            new Date(messageJson.getDate().getTime() * 1000L),
                            new Time(messageJson.getDate().getTime() * 1000L)
                    )
            );
        }
    }
}
