package mavmi.telegram_bot.shakal.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.database_starter.model.RequestModel;
import mavmi.telegram_bot.lib.database_starter.model.UserModel;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.UserJson;
import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.secured_starter.secured.api.Secured;
import mavmi.telegram_bot.lib.service_api.Service;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.lib.user_cache_starter.aop.api.SetupUserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.ApolocheseServiceModule;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.DiceServiceModule;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.HoroscopeServiceModule;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.shakal.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;

/**
 * Shakal bot service entrypoint
 */
@Slf4j
@Component
public class ShakalService implements Service<ShakalServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<ShakalServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @Autowired
    private UserCaches userCaches;

    public ShakalService(
            CommonServiceModule commonServiceModule,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule,
            MainMenuServiceModule mainMenuServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(ShakalServiceMenu.APOLOCHEESE, apolocheseServiceModule)
                .add(ShakalServiceMenu.DICE, diceServiceModule)
                .add(ShakalServiceMenu.HOROSCOPE, horoscopeServiceModule)
                .add(ShakalServiceMenu.MAIN_MENU, mainMenuServiceModule);
    }

    @SetupUserCaches
    @Secured
    @Override
    public void handleRequest(ShakalServiceRq shakalServiceRq) {
        updateDatabase(shakalServiceRq);

        String msg = null;
        UserJson userJson = shakalServiceRq.getUserJson();
        ShakalDataCache dataCache = userCaches.getDataCache(ShakalDataCache.class);
        Menu menu = dataCache.getMenu();
        if (userJson != null) {
            msg = shakalServiceRq.getMessageJson().getTextMessage();
        }

        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                shakalServiceRq.getChatId(),
                shakalServiceRq.getUserJson().getUsername(),
                shakalServiceRq.getUserJson().getFirstName(),
                shakalServiceRq.getUserJson().getLastName(),
                msg
        );

        ServiceModule<ShakalServiceRq> serviceElement = serviceComponentsContainer.getModule(menu);
        serviceElement.handleRequest(shakalServiceRq);
    }

    private void updateDatabase(ShakalServiceRq shakalServiceRq) {
        UserJson userJson = shakalServiceRq.getUserJson();
        MessageJson messageJson = shakalServiceRq.getMessageJson();

        if (userJson != null) {
            commonServiceModule.getUserRepository().save(
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
            commonServiceModule.getRequestRepository().save(
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
