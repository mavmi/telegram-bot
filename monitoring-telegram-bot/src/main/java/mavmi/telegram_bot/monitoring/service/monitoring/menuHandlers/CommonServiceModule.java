package mavmi.telegram_bot.monitoring.service.monitoring.menuHandlers;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.telegram_bot.lib.database_starter.auth.UserAuthentication;
import mavmi.telegram_bot.lib.database_starter.model.RuleModel;
import mavmi.telegram_bot.lib.database_starter.repository.CertificateRepository;
import mavmi.telegram_bot.lib.database_starter.repository.RuleRepository;
import mavmi.telegram_bot.lib.dto.service.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.lib.menu_engine_starter.engine.MenuEngine;
import mavmi.telegram_bot.lib.menu_engine_starter.menu.container.MenuHistoryContainer;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.UserCaches;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.monitoring.cache.dto.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import mavmi.telegram_bot.monitoring.mapper.CryptoMapper;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.AsyncTaskService;
import mavmi.telegram_bot.monitoring.service.asyncTaskService.ServiceTask;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Component
@RequiredArgsConstructor
public class CommonServiceModule {

    private final UserCachesProvider userCachesProvider;
    private final MenuEngine menuEngine;
    private final CryptoMapper cryptoMapper;
    private final MonitoringTelegramBotSender sender;
    private final RuleRepository ruleRepository;
    private final CertificateRepository certificateRepository;
    private final AsyncTaskService asyncTaskService;
    private final UserAuthentication userAuthentication;
    private final RemoteParameterPlugin remoteParameterPlugin;

    private String certificatesOutputDirectory;
    private TextEncryptor textEncryptor;
    private MonitoringConstants constants;

    @Autowired
    public void setup(MonitoringConstantsHandler constantsHandler,
                      @Qualifier("rocketChatTextEncryptor") TextEncryptor textEncryptor,
                      @Value("${certificates.output-directory}") String certificatesOutputDirectory) {
        this.constants = constantsHandler.get();
        this.textEncryptor = textEncryptor;
        this.certificatesOutputDirectory = certificatesOutputDirectory;
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
        sendText(request.getChatId(), constants.getPhrases().getCommon().getError());
    }

    public void sendText(long chatId, String msg) {
        sender.sendText(chatId, msg);
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboard) {
        sender.sendReplyKeyboard(chatId, msg, keyboard);
    }

    public void sendCurrentMenuButtons(long chatId) {
        MonitoringDataCache dataCache = getUserCaches().getDataCache(MonitoringDataCache.class);
        MonitoringServiceMenu menu = (MonitoringServiceMenu) dataCache.getMenuHistoryContainer().getLast();

        String[] buttons = menuEngine.getMenuButtons(menu).toArray(new String[0]);
        sendReplyKeyboard(chatId,
                constants.getPhrases().getCommon().getAvailableOptions(),
                buttons);
    }

    public void sendFile(long chatId, File file) {
        sender.sendFile(chatId, file);
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
        menuHistoryContainer.deleteUntil(MonitoringServiceMenu.class, menuHistoryContainer.getLast(MonitoringServiceMenu.class).getParent());

        // Reset messages history
        dataCache.getMessagesContainer().clearMessages();
    }
}
