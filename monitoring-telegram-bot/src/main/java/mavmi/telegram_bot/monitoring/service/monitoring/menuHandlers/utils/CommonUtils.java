package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.database_starter.model.RuleModel;
import mavmi.telegram_bot.lib.database_starter.repository.RuleRepository;
import mavmi.telegram_bot.lib.dto.service.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.menu.container.MenuHistoryContainer;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.ServiceTask;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Getter
@Component
@RequiredArgsConstructor
public class CommonUtils {

    private final PmsUtils pmsUtils;
    private final TelegramBotUtils telegramBotUtils;
    private final UserCachesProvider userCachesProvider;
    private final MenuEngine menuEngine;
    private final MonitoringTelegramBotSender sender;
    private final RuleRepository ruleRepository;
    private final AsyncTaskService asyncTaskService;
    private final UserAuthentication userAuthentication;
    private final RemoteParameterPlugin remoteParameterPlugin;

    private MonitoringConstants constants;

    @Autowired
    public void setup(MonitoringConstantsHandler constantsHandler) {
        this.constants = constantsHandler.get();
    }

    public UserCaches getUserCaches() {
        return userCachesProvider.get();
    }

    public void postTask(MonitoringServiceRq request) {
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

        sendCurrentMenuButtons(request.getChatId());
    }

    public void exit(MonitoringServiceRq request) {
        dropUserCaches();
        sendCurrentMenuButtons(request.getChatId());
    }

    public void error(MonitoringServiceRq request) {
        telegramBotUtils.sendText(request.getChatId(), constants.getPhrases().getCommon().getError());
    }

    public void sendCurrentMenuButtons(long chatId) {
        sendCurrentMenuButtons(chatId, constants.getPhrases().getCommon().getAvailableOptions());
    }

    public void sendCurrentMenuButtons(long chatId, String msg) {
        MonitoringDataCache dataCache = getUserCaches().getDataCache(MonitoringDataCache.class);
        MonitoringServiceMenu menu = (MonitoringServiceMenu) dataCache.getMenuHistoryContainer().getLast();
        List<String> keyboard = null;

        if (menu == MonitoringServiceMenu.PMS_MAIN) {
            keyboard = Stream.concat(pmsUtils.retrieveAllParamsNames().stream(),
                            Stream.of(menuEngine.getMenuButtonByName(MonitoringServiceMenu.PMS_MAIN, "go_back").getValue()))
                    .toList();
        } else {
            keyboard = menuEngine.getMenuButtonsAsString(menu);
        }

        telegramBotUtils.sendReplyKeyboard(chatId, msg, keyboard);
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
        MonitoringDataCache dataCache = getUserCaches().getDataCache(MonitoringDataCache.class);

        // Drop menu level
        MenuHistoryContainer menuHistoryContainer = dataCache.getMenuHistoryContainer();
        menuHistoryContainer.deleteUntil(MonitoringServiceMenu.class, MonitoringServiceMenu.MAIN_MENU);

        // Reset messages history
        dataCache.getMessagesContainer().clear();
    }
}
