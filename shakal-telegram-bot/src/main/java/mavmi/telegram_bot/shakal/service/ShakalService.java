package mavmi.telegram_bot.shakal.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.service.Service;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.UserJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalAuthCache;
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
    private CacheComponent cacheComponent;

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
        ShakalDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(ShakalDataCache.class);
        Menu menu = dataCache.getMenuContainer().getLast();
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

    @Override
    public DataCache initDataCache(long chatId) {
        return new ShakalDataCache(chatId, ShakalServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new ShakalAuthCache(true);
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
