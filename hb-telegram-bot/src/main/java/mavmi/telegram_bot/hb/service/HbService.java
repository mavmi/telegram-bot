package mavmi.telegram_bot.hb.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.aop.cache.api.SetupUserCaches;
import mavmi.telegram_bot.common.cache.api.AuthCache;
import mavmi.telegram_bot.common.cache.api.DataCache;
import mavmi.telegram_bot.common.service.container.direct.impl.MenuToServiceModuleContainer;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.HB_SERVICE_TASK;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.common.service.service.direct.DirectService;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.hb.cache.HbAuthCache;
import mavmi.telegram_bot.hb.cache.HbDataCache;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import mavmi.telegram_bot.hb.service.menu.HbServiceMenu;
import mavmi.telegram_bot.hb.service.serviceModule.MainMenuServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.auth.AuthServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.fortune.FortuneGetPriseIdServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.fortune.FortuneGetUsernameServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.note.NoteGetEventGroupServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.note.NoteGetEventServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.note.NoteGetPeerUsernameServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.note.NoteGradeServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.score.ScoreServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class HbService implements DirectService<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final MenuToServiceModuleContainer<HbServiceResponse, HbServiceRequest> menuToServiceModuleContainer;

    public HbService(
            CommonServiceModule commonServiceModule,
            MainMenuServiceModule mainMenuServiceModule,
            AuthServiceModule authServiceModule,
            NoteGetPeerUsernameServiceModule noteGetPeerUsernameServiceModule,
            NoteGetEventGroupServiceModule noteGetEventGroupServiceModule,
            NoteGetEventServiceModule noteGetEventServiceModule,
            NoteGradeServiceModule noteGradeServiceModule,
            ScoreServiceModule scoreServiceModule,
            FortuneGetUsernameServiceModule fortuneGetUsernameServiceModule,
            FortuneGetPriseIdServiceModule fortuneGetPriseIdServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        menuToServiceModuleContainer = new MenuToServiceModuleContainer<>(
                Map.of(
                        HbServiceMenu.MAIN_MENU, mainMenuServiceModule,
                        HbServiceMenu.AUTH, authServiceModule,
                        HbServiceMenu.NOTE_USERNAME, noteGetPeerUsernameServiceModule,
                        HbServiceMenu.NOTE_EVENT_GROUPS, noteGetEventGroupServiceModule,
                        HbServiceMenu.NOTE_EVENT, noteGetEventServiceModule,
                        HbServiceMenu.NOTE_GRADE, noteGradeServiceModule,
                        HbServiceMenu.SCORE, scoreServiceModule,
                        HbServiceMenu.FORTUNE_USERNAME, fortuneGetUsernameServiceModule,
                        HbServiceMenu.FORTUNE_PRISE_ID, fortuneGetPriseIdServiceModule
                )
        );
    }

    @Override
    @SetupUserCaches
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        HbDataCache dataCache =  commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);

        log.info("Got request from id: {}", dataCache.getUserId());

        if (request.getMessageJson().getTextMessage().equals(commonServiceModule.getConstants().getRequests().getCancel())) {
            commonServiceModule.dropMenu();
            dataCache.getMessagesContainer().clearMessages();

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getCancel())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        }

        Menu menu = dataCache.getMenuContainer().getLast();
        ServiceModule<HbServiceResponse, HbServiceRequest> module = menuToServiceModuleContainer.get(menu);
        return module.handleRequest(request);
    }

    @Override
    public DataCache initDataCache(long chatId) {
        return new HbDataCache(chatId, HbServiceMenu.MAIN_MENU);
    }

    @Override
    public AuthCache initAuthCache(long chatId) {
        return new HbAuthCache(true);
    }
}
