package mavmi.telegram_bot.shakal.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.aop.secured.api.Secured;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.cache.impl.CacheContainer;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.service.container.direct.impl.MenuToServiceModuleContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.UserJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.direct.DirectService;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.shakal.cache.ShakalServiceAuthCache;
import mavmi.telegram_bot.shakal.cache.ShakalServiceDataCache;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import mavmi.telegram_bot.shakal.service.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.serviceModule.ApolocheseServiceModule;
import mavmi.telegram_bot.shakal.service.serviceModule.DiceServiceModule;
import mavmi.telegram_bot.shakal.service.serviceModule.HoroscopeServiceModule;
import mavmi.telegram_bot.shakal.service.serviceModule.MainMenuServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.Map;

@Slf4j
@Component
public class ShakalDirectService implements DirectService<ShakalServiceRs, ShakalServiceRq> {

    private final UserAuthentication userAuthentication;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CacheContainer cacheContainer;
    private final MenuToServiceModuleContainer<ShakalServiceRs, ShakalServiceRq> menuToServiceModuleContainer;

    @Autowired
    private CacheComponent cacheComponent;

    public ShakalDirectService(
            UserAuthentication userAuthentication,
            UserRepository userRepository,
            RequestRepository requestRepository,
            CacheContainer cacheContainer,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule,
            MainMenuServiceModule mainMenuServiceModule
    ) {
        this.userAuthentication = userAuthentication;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.cacheContainer = cacheContainer;
        this.menuToServiceModuleContainer = new MenuToServiceModuleContainer<>(
                Map.of(
                        ShakalServiceMenu.APOLOCHEESE, apolocheseServiceModule,
                        ShakalServiceMenu.DICE, diceServiceModule,
                        ShakalServiceMenu.HOROSCOPE, horoscopeServiceModule,
                        ShakalServiceMenu.MAIN_MENU, mainMenuServiceModule
                )
        );
    }

    @SetupUserCaches
    @Secured
    @Override
    public ShakalServiceRs handleRequest(ShakalServiceRq shakalServiceRq) {
        updateDatabase(shakalServiceRq);

        String msg = null;
        UserJson userJson = shakalServiceRq.getUserJson();
        ShakalServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(ShakalServiceDataCache.class);
        Menu menu = dataCache.getMenuContainer().getLast();
        if (userJson != null) {
            msg = shakalServiceRq.getMessageJson().getTextMessage();
        }

        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                dataCache.getUserId(),
                dataCache.getUsername(),
                dataCache.getFirstName(),
                dataCache.getLastName(),
                msg
        );

        ServiceModule<ShakalServiceRs, ShakalServiceRq> serviceElement = menuToServiceModuleContainer.get(menu);
        return serviceElement.handleRequest(shakalServiceRq);
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return new ShakalServiceDataCache(chatId, ShakalServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new ShakalServiceAuthCache(true);
    }

    private void updateDatabase(ShakalServiceRq shakalServiceRq) {
        UserJson userJson = shakalServiceRq.getUserJson();
        MessageJson messageJson = shakalServiceRq.getMessageJson();

        if (userJson != null) {
            userRepository.save(
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
            requestRepository.save(
                    new RequestModel(
                            0L,
                            userJson.getId(),
                            messageJson.getTextMessage(),
                            new Date(messageJson.getDate().getTime() * 1000L),
                            new Time(messageJson.getDate().getTime() * 1000L)
                    )
            );
        }
    }
}
