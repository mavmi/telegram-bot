package mavmi.telegram_bot.monitoring.service.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.MONITORING_SERVICE_TASK;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.service.AbstractService;
import mavmi.telegram_bot.common.service.menu.IMenu;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.ServiceTask;
import mavmi.telegram_bot.monitoring.service.cache.UserDataCache;
import mavmi.telegram_bot.monitoring.service.constants.Buttons;
import mavmi.telegram_bot.monitoring.service.constants.Phrases;
import mavmi.telegram_bot.monitoring.service.constants.Requests;
import mavmi.telegram_bot.monitoring.service.service.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MonitoringService extends AbstractService {

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

    private final RuleRepository ruleRepository;
    private final AsyncTaskService asyncTaskService;

    @Autowired
    private UserSession userSession;

    @SneakyThrows
    public MonitoringServiceRs handleRequest(MonitoringServiceRq monitoringServiceRq) {
        long chatId = monitoringServiceRq.getChatId();
        String msg = monitoringServiceRq.getMessageJson().getTextMessage();

        UserDataCache userCache = userSession.getCache();
        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return error();
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
            return switch (msg) {
                case Requests.HOST_REQ -> handleHostReq();
                case Requests.APPS_REQ -> handleAppsReq();
                default -> error();
            };
        } else if (msg.equals(Buttons.EXIT_BTN)) {
            return exit();
        } else {
            AsyncTaskManagerJson asyncTaskManagerJson = monitoringServiceRq.getAsyncTaskManagerJson();
            asyncTaskService.put(
                    asyncTaskManagerJson.getTarget(),
                    ServiceTask
                            .builder()
                            .initiatorId(chatId)
                            .message(asyncTaskManagerJson.getMessage())
                            .target(asyncTaskManagerJson.getTarget())
                            .build()
            );

            return createSendKeyboardResponse(
                    Phrases.OK_MSG,
                    (userCache.getMenu() == Menu.HOST) ? HOST_BUTTONS : APPS_BUTTONS
            );
        }
    }

    public List<Long> getAvailableIdx() {
        List<Long> idx = new ArrayList<>();
        List<RuleModel> ruleModelList = ruleRepository.findAll();

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

    private MonitoringServiceRs handleHostReq() {
        userSession.getCache().setMenu(Menu.HOST);
        return createSendKeyboardResponse(Phrases.AVAILABLE_OPTIONS_MSG, HOST_BUTTONS);
    }

    private MonitoringServiceRs handleAppsReq() {
        userSession.getCache().setMenu(Menu.APPS);
        return createSendKeyboardResponse(Phrases.AVAILABLE_OPTIONS_MSG, APPS_BUTTONS);
    }

    private MonitoringServiceRs error() {
        return createSendTextResponse(Phrases.ERR_MSG);
    }

    private MonitoringServiceRs exit() {
        dropUserInfo();
        return createSendTextResponse(Phrases.OK_MSG);
    }

    private void dropUserInfo() {
        userSession.getCache().setMenu(Menu.MAIN_MENU);
        userSession.getCache().getMessagesHistory().clear();
    }

    private MonitoringServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return MonitoringServiceRs
                .builder()
                .monitoringServiceTask(MONITORING_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    private MonitoringServiceRs createSendKeyboardResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return MonitoringServiceRs
                .builder()
                .monitoringServiceTask(MONITORING_SERVICE_TASK.SEND_KEYBOARD)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();
    }
}
