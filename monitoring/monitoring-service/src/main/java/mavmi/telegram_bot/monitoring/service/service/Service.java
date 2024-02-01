package mavmi.telegram_bot.monitoring.service.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.service.AbstractService;
import mavmi.telegram_bot.common.service.menu.IMenu;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.ServiceTask;
import mavmi.telegram_bot.monitoring.service.cache.UserDataCache;
import mavmi.telegram_bot.monitoring.service.constants.Buttons;
import mavmi.telegram_bot.monitoring.service.constants.Phrases;
import mavmi.telegram_bot.monitoring.service.constants.Requests;
import mavmi.telegram_bot.monitoring.service.httpClient.HttpClient;
import mavmi.telegram_bot.monitoring.service.service.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Service extends AbstractService {

    private static final String[] HOST_BUTTONS = new String[] {
            Buttons.MEM_BTN,
            Buttons.RAM_BTN,
            Buttons.USERS_BTN,
            Buttons.BACKUP_BTN,
            Buttons.EXIT_BTN
    };

    private static final String[] APPS_BUTTONS = new String[] {
            Buttons.PK_BTN,
            Buttons.FP_BTN,
            Buttons.GC_BTN,
            Buttons.EXIT_BTN
    };

    private final HttpClient httpClient;
    private final RuleRepository ruleRepository;
    private final AsyncTaskService asyncTaskService;

    @Autowired
    private UserSession userSession;

    @SneakyThrows
    public int handleRequest(MonitoringServiceRq monitoringServiceRq) {
        long chatId = monitoringServiceRq.getChatId();
        String msg = monitoringServiceRq.getMessageJson().getTextMessage();

        UserDataCache userCache = userSession.getCache();
        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return HttpStatus.BAD_REQUEST.value();
        }

        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );

        IMenu userMenu = userCache.getMenu();
        if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case Requests.HOST_REQ -> handleHostReq(userCache);
                case Requests.APPS_REQ -> handleAppsReq(userCache);
                default -> error(userCache);
            }
        } else if (msg.equals(Buttons.EXIT_BTN)) {
            exit(userCache);
        } else {
            AsyncTaskManagerJson asyncTaskManagerJson = monitoringServiceRq.getAsyncTaskManagerJson();
            httpClient.sendKeyboard(
                    chatId,
                    Phrases.OK_MSG,
                    (userCache.getMenu() == Menu.HOST) ? HOST_BUTTONS : APPS_BUTTONS
            );

            asyncTaskService.put(
                    asyncTaskManagerJson.getTarget(),
                    ServiceTask
                            .builder()
                            .initiatorId(chatId)
                            .message(asyncTaskManagerJson.getMessage())
                            .target(asyncTaskManagerJson.getTarget())
                            .build()
            );
        }

        return HttpStatus.OK.value();
    }

    public List<Long> getAvailableIdx() {
        List<Long> idx = new ArrayList<>();
        List<RuleModel> ruleModelList = ruleRepository.getAll();

        for (RuleModel ruleModel : ruleModelList) {
            Long userId = ruleModel.getUserid();
            Boolean value = ruleModel.getMonitoring();

            if (value != null && value) {
                idx.add(userId);
            }
        }

        return idx;
    }

    @Override
    public AbstractUserDataCache initCache() {
        return new UserDataCache(userSession.getId(), Menu.MAIN_MENU);
    }

    private void handleHostReq(UserDataCache user) {
        user.setMenu(Menu.HOST);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.AVAILABLE_OPTIONS_MSG,
                HOST_BUTTONS
        );
    }

    private void handleAppsReq(UserDataCache user) {
        user.setMenu(Menu.APPS);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.AVAILABLE_OPTIONS_MSG,
                APPS_BUTTONS
        );
    }

    private void error(UserDataCache user) {
        httpClient.sendText(
                List.of(user.getUserId()),
                Phrases.ERR_MSG
        );
    }

    private void exit(UserDataCache user) {
        dropUserInfo(user);
        httpClient.sendText(
                List.of(user.getUserId()),
                Phrases.OK_MSG
        );
    }

    private void dropUserInfo(UserDataCache user) {
        user.setMenu(Menu.MAIN_MENU);
        user.getMessagesHistory().clear();
    }
}
