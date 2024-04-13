package mavmi.telegram_bot.shakal.service.service.shakal;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.UserDataCache;
import mavmi.telegram_bot.common.database.model.RequestModel;
import mavmi.telegram_bot.common.database.model.UserModel;
import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.service.container.impl.MenuToServiceServiceModuleContainer;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.AbstractService;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.shakal.service.cache.ShakalServiceUserDataCache;
import mavmi.telegram_bot.shakal.service.service.shakal.menu.ShakalServiceMenu;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.ApolocheseServiceModule;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.DiceServiceModule;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.HoroscopeServiceModule;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.shakal.service.service.shakal.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.Time;
import java.util.Map;

@Slf4j
@Component
public class ShakalService extends AbstractService {

    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final CommonServiceModule commonServiceModule;
    private final MenuToServiceServiceModuleContainer<ShakalServiceRs, ShakalServiceRq> menuToServiceServiceModuleContainer;

    public ShakalService(
            UserRepository userRepository,
            RequestRepository requestRepository,
            CommonServiceModule commonServiceModule,
            ApolocheseServiceModule apolocheseServiceModule,
            DiceServiceModule diceServiceModule,
            HoroscopeServiceModule horoscopeServiceModule,
            MainMenuServiceModule mainMenuServiceModule
    ) {
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        this.commonServiceModule = commonServiceModule;
        this.menuToServiceServiceModuleContainer = new MenuToServiceServiceModuleContainer<>(
                Map.of(
                        ShakalServiceMenu.APOLOCHEESE, apolocheseServiceModule,
                        ShakalServiceMenu.DICE, diceServiceModule,
                        ShakalServiceMenu.HOROSCOPE, horoscopeServiceModule,
                        ShakalServiceMenu.MAIN_MENU, mainMenuServiceModule
                )
        );
    }

    public ShakalServiceRs handleRequest(ShakalServiceRq shakalServiceRq) {
        updateDatabase(shakalServiceRq);

        String msg = null;
        UserJson userJson = shakalServiceRq.getUserJson();
        if (userJson != null) {
            msg = shakalServiceRq.getMessageJson().getTextMessage();
        }

        ShakalServiceUserDataCache userCache = commonServiceModule.getUserSession().getCache();
        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );

        Menu menu = userCache.getMenuContainer().getLast();
        ServiceModule<ShakalServiceRs, ShakalServiceRq> serviceElement = menuToServiceServiceModuleContainer.get(menu);
        return serviceElement.process(shakalServiceRq);
    }

    @Override
    public UserDataCache initCache() {
        return new ShakalServiceUserDataCache(commonServiceModule.getUserSession().getId(), ShakalServiceMenu.MAIN_MENU);
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
