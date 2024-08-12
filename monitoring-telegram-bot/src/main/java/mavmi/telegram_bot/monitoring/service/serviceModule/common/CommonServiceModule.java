package mavmi.telegram_bot.monitoring.service.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.service.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.MONITORING_SERVICE_TASK;
import mavmi.telegram_bot.monitoring.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.asyncTaskService.ServiceTask;
import mavmi.telegram_bot.monitoring.cache.MonitoringServiceDataCache;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringServiceConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringServiceConstants;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
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
    private CacheComponent cacheComponent;

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
        MonitoringServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringServiceDataCache.class);
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

        return createSendReplyKeyboardResponse(
                constants.getPhrases().getOk(),
                (dataCache.getMenuContainer().getLast() == MonitoringServiceMenu.HOST) ? hostButtons : appsButtons
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
        MonitoringServiceDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringServiceDataCache.class);

        dataCache.getMenuContainer().removeLast();
        dataCache.getMessagesContainer().clearMessages();
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

    public MonitoringServiceRs createSendReplyKeyboardResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return MonitoringServiceRs
                .builder()
                .monitoringServiceTask(MONITORING_SERVICE_TASK.SEND_KEYBOARD)
                .messageJson(messageJson)
                .replyKeyboardJson(replyKeyboardJson)
                .build();
    }
}
