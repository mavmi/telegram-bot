package mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.MONITORING_SERVICE_TASK;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.httpFilter.userSession.session.UserSession;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.ServiceTask;
import mavmi.telegram_bot.monitoring.service.cache.MonitoringServiceUserDataCache;
import mavmi.telegram_bot.monitoring.service.constants.Buttons;
import mavmi.telegram_bot.monitoring.service.constants.Phrases;
import mavmi.telegram_bot.monitoring.service.service.monitoring.menu.MonitoringServiceMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@RequiredArgsConstructor
public class CommonServiceModule {

    public static final String[] HOST_BUTTONS = new String[] {
            Buttons.MEM_BTN,
            Buttons.RAM_BTN,
            Buttons.USERS_BTN,
            Buttons.BACKUP_BTN,
            Buttons.EXIT_BTN
    };

    public static final String[] APPS_BUTTONS = new String[] {
            Buttons.PK_BTN,
            Buttons.FP_BTN,
            Buttons.GC_BTN,
            Buttons.EXIT_BTN
    };

    private final RuleRepository ruleRepository;
    private final AsyncTaskService asyncTaskService;

    @Autowired
    private UserSession userSession;

    public MonitoringServiceRs postTask(MonitoringServiceRq request) {
        MonitoringServiceUserDataCache userCache = userSession.getCache();
        AsyncTaskManagerJson asyncTaskManagerJson = request.getAsyncTaskManagerJson();

        asyncTaskService.put(
                asyncTaskManagerJson.getTarget(),
                ServiceTask
                        .builder()
                        .initiatorId(request.getChatId())
                        .message(asyncTaskManagerJson.getMessage())
                        .target(asyncTaskManagerJson.getTarget())
                        .build()
        );

        return createSendKeyboardResponse(
                Phrases.OK_MSG,
                (userCache.getMenuContainer().getLast() == MonitoringServiceMenu.HOST) ? HOST_BUTTONS : APPS_BUTTONS
        );
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

    public MonitoringServiceRs exit(MonitoringServiceRq request) {
        dropUserInfo();
        return createSendTextResponse(Phrases.OK_MSG);
    }

    public void dropUserInfo() {
        MonitoringServiceUserDataCache userCache = userSession.getCache();

        userCache.getMenuContainer().removeLast();
        userCache.getMessagesContainer().clearMessages();
    }

    public MonitoringServiceRs error(MonitoringServiceRq request) {
        return createSendTextResponse(Phrases.ERR_MSG);
    }

    public MonitoringServiceRs createSendTextResponse(String msg) {
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

    public MonitoringServiceRs createSendKeyboardResponse(String msg, String[] keyboardButtons) {
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
