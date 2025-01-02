package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.service.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.monitoring.asyncTaskService.service.AsyncTaskService;
import mavmi.telegram_bot.monitoring.asyncTaskService.service.ServiceTask;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Component
public class CommonServiceModule {

    private final MonitoringTelegramBotSender sender;
    private final RuleRepository ruleRepository;
    private final AsyncTaskService asyncTaskService;
    private final MonitoringConstants constants;
    private final UserAuthentication userAuthentication;
    private final String[] hostButtons;
    private final String[] appsButtons;

    @Autowired
    private CacheComponent cacheComponent;

    public CommonServiceModule(
            MonitoringTelegramBotSender sender,
            RuleRepository ruleRepository,
            AsyncTaskService asyncTaskService,
            MonitoringConstantsHandler constantsHandler,
            UserAuthentication userAuthentication
    ) {
        this.sender = sender;
        this.ruleRepository = ruleRepository;
        this.asyncTaskService = asyncTaskService;
        this.constants = constantsHandler.get();
        this.userAuthentication = userAuthentication;
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

    public void postTask(MonitoringServiceRq request) {
        MonitoringDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringDataCache.class);
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

        sendReplyKeyboard(
                request.getChatId(),
                constants.getPhrases().getOk(),
                (dataCache.getMenuContainer().getLast() == MonitoringServiceMenu.HOST) ? hostButtons : appsButtons
        );
    }

    public void exit(MonitoringServiceRq request) {
        dropUserCaches();
        sendText(request.getChatId(), constants.getPhrases().getOk());
    }

    public void error(MonitoringServiceRq request) {
        sendText(request.getChatId(), constants.getPhrases().getError());
    }

    public void sendText(long chatId, String msg) {
        sender.sendText(chatId, msg);
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboard) {
        sender.sendReplyKeyboard(chatId, msg, keyboard);
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

    public void dropUserCaches() {
        MonitoringDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringDataCache.class);

        dataCache.getMenuContainer().removeLast();
        dataCache.getMessagesContainer().clearMessages();
    }
}
