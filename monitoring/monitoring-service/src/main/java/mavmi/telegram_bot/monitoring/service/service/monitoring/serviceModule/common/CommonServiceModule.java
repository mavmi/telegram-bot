package mavmi.telegram_bot.monitoring.service.service.monitoring.serviceModule.common;

import lombok.Getter;
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
import mavmi.telegram_bot.monitoring.service.constantsHandler.MonitoringServiceConstantsHandler;
import mavmi.telegram_bot.monitoring.service.constantsHandler.dto.MonitoringServiceConstants;
import mavmi.telegram_bot.monitoring.service.service.monitoring.menu.MonitoringServiceMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class CommonServiceModule {

    private final RuleRepository ruleRepository;
    private final AsyncTaskService asyncTaskService;
    private final MonitoringServiceConstants constants;
    private final String[] hostButtons;
    private final String[] appsButtons;

    @Autowired
    private UserSession userSession;

    public CommonServiceModule(
            RuleRepository ruleRepository,
            AsyncTaskService asyncTaskService,
            MonitoringServiceConstantsHandler constantsHandler
    ) {
        this.ruleRepository = ruleRepository;
        this.asyncTaskService = asyncTaskService;
        this.constants = constantsHandler.get();
        this.hostButtons = new String[] {
                constants.getButtons().getMemoryInfo(),
                constants.getButtons().getRamInfo(),
                constants.getButtons().getUsersInfo(),
                constants.getButtons().getBackup(),
                constants.getButtons().getExit()
        };
        this.appsButtons = new String[] {
                constants.getButtons().getPk(),
                constants.getButtons().getFp(),
                constants.getButtons().getGc(),
                constants.getButtons().getExit()
        };
    }

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
                constants.getPhrases().getOk(),
                (userCache.getMenuContainer().getLast() == MonitoringServiceMenu.HOST) ? hostButtons : appsButtons
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
        return createSendTextResponse(constants.getPhrases().getOk());
    }

    public void dropUserInfo() {
        MonitoringServiceUserDataCache userCache = userSession.getCache();

        userCache.getMenuContainer().removeLast();
        userCache.getMessagesContainer().clearMessages();
    }

    public MonitoringServiceRs error(MonitoringServiceRq request) {
        return createSendTextResponse(constants.getPhrases().getError());
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
