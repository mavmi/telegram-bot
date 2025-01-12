package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common;

import lombok.Getter;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.PrivilegesRepository;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.monitoring.asyncTaskService.service.AsyncTaskService;
import mavmi.telegram_bot.monitoring.asyncTaskService.service.ServiceTask;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.PrivilegesManagement;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.UserPrivileges;
import mavmi.telegram_bot.monitoring.constantsHandler.MonitoringConstantsHandler;
import mavmi.telegram_bot.monitoring.constantsHandler.dto.MonitoringConstants;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Getter
@Component
public class CommonServiceModule {

    private final MonitoringTelegramBotSender sender;
    private final RuleRepository ruleRepository;
    private final PrivilegesRepository privilegesRepository;
    private final AsyncTaskService asyncTaskService;
    private final MonitoringConstants constants;
    private final UserAuthentication userAuthentication;
    private final RemoteParameterPlugin remoteParameterPlugin;
    private final String[] hostButtons;
    private final String[] appsButtons;
    private final String[] privilegesInitButtons;
    private final String[] privilegesButtons;
    private final String[] privilegesAddButtons;
    private final String[] pmsButtons;
    private final String[] pmsEditButtons;
    private final String[] botAccessInitButtons;
    private final String[] botAccessButtons;
    private final Map<MonitoringServiceMenu, String[]> menuToButtons;

    @Autowired
    private CacheComponent cacheComponent;

    public CommonServiceModule(
            MonitoringTelegramBotSender sender,
            RuleRepository ruleRepository,
            PrivilegesRepository privilegesRepository,
            AsyncTaskService asyncTaskService,
            MonitoringConstantsHandler constantsHandler,
            UserAuthentication userAuthentication,
            RemoteParameterPlugin remoteParameterPlugin
    ) {
        this.sender = sender;
        this.ruleRepository = ruleRepository;
        this.privilegesRepository = privilegesRepository;
        this.asyncTaskService = asyncTaskService;
        this.constants = constantsHandler.get();
        this.userAuthentication = userAuthentication;
        this.remoteParameterPlugin = remoteParameterPlugin;

        this.hostButtons = new String[] {
                constants.getButtons().getServerInfo().getMemoryInfo(),
                constants.getButtons().getServerInfo().getRamInfo(),
                constants.getButtons().getServerInfo().getUsersInfo(),
                constants.getButtons().getServerInfo().getBackup(),
                constants.getButtons().getCommon().getExit()
        };
        this.appsButtons = new String[] {
                constants.getButtons().getApps().getPk(),
                constants.getButtons().getApps().getFp(),
                constants.getButtons().getApps().getGc(),
                constants.getButtons().getCommon().getExit()
        };
        this.privilegesInitButtons = new String[] {
                constants.getButtons().getCommon().getExit()
        };
        this.privilegesButtons = new String[] {
                constants.getButtons().getPrivileges().getInfo(),
                constants.getButtons().getPrivileges().getAddPrivilege(),
                constants.getButtons().getPrivileges().getDeletePrivilege(),
                constants.getButtons().getCommon().getExit()
        };
        this.privilegesAddButtons = Stream.concat(
                Arrays.stream(PRIVILEGE.values()).map(PRIVILEGE::getName),
                Stream.of(constants.getButtons().getCommon().getExit())
        ).toArray(String[]::new);
        this.pmsButtons = Stream.concat(
                Arrays.stream(constants.getButtons().getPms().getParameters()),
                Stream.of(constants.getButtons().getCommon().getExit())
        ).toArray(String[]::new);
        this.pmsEditButtons = new String[] {
                constants.getButtons().getPms().getInfo(),
                constants.getButtons().getCommon().getExit()
        };
        this.botAccessInitButtons = new String[] {
                constants.getButtons().getCommon().getExit()
        };
        this.botAccessButtons = new String[] {
                constants.getButtons().getBotAccess().getInfo(),
                constants.getButtons().getBotAccess().getAddWaterStuff(),
                constants.getButtons().getBotAccess().getRevokeWaterStuff(),
                constants.getButtons().getBotAccess().getAddMonitoring(),
                constants.getButtons().getBotAccess().getRevokeMonitoring(),
                constants.getButtons().getCommon().getExit()
        };

        menuToButtons = Map.of(
                MonitoringServiceMenu.HOST, hostButtons,
                MonitoringServiceMenu.APPS, appsButtons,
                MonitoringServiceMenu.PRIVILEGES_INIT, privilegesInitButtons,
                MonitoringServiceMenu.PRIVILEGES, privilegesButtons,
                MonitoringServiceMenu.PRIVILEGES_ADD, privilegesAddButtons,
                MonitoringServiceMenu.PMS, pmsButtons,
                MonitoringServiceMenu.PMS_EDIT, pmsEditButtons,
                MonitoringServiceMenu.BOT_ACCESS_INIT, botAccessInitButtons,
                MonitoringServiceMenu.BOT_ACCESS, botAccessButtons
        );
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
                constants.getPhrases().getCommon().getOk(),
                (dataCache.getMenu() == MonitoringServiceMenu.HOST) ? hostButtons : appsButtons
        );
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
        sendCurrentMenuButtons(chatId, getConstants().getPhrases().getCommon().getAvailableOptions());
    }

    public void sendCurrentMenuButtons(long chatId, String textMessage) {
        MonitoringDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringDataCache.class);
        MonitoringServiceMenu menu = (MonitoringServiceMenu) dataCache.getMenu();

        if (menu == MonitoringServiceMenu.MAIN_MENU) {
            String[] availableOptions = getAvailableOptions();
            if (availableOptions.length == 0) {
                return;
            }

            sendReplyKeyboard(chatId, textMessage, availableOptions);
        } else if (menu == MonitoringServiceMenu.PRIVILEGES_DELETE) {
            PrivilegesManagement cachedPrivilegesManagement = dataCache.getPrivilegesManagement();

            String message;
            String[] buttons;
            if (cachedPrivilegesManagement.getWorkingPrivileges().isEmpty()) {
                message = constants.getPhrases().getPrivileges().getNoPrivileges();
                buttons = new String[] { constants.getButtons().getCommon().getExit() };
            } else {
                message = constants.getPhrases().getPrivileges().getSelectPrivilege();
                buttons = Stream.concat(
                        cachedPrivilegesManagement
                                .getWorkingPrivileges().stream()
                                .map(PRIVILEGE::getName),
                        Stream.of(constants.getButtons().getCommon().getExit())
                ).toArray(String[]::new);
            }

            sendReplyKeyboard(chatId, message, buttons);
        } else {
            String[] buttons = menuToButtons.get(menu);
            sendReplyKeyboard(chatId, textMessage, buttons);
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

    public void dropUserCaches() {
        MonitoringDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringDataCache.class);

        Menu parentMenu = dataCache.getMenu().getParent();
        if (parentMenu != null) {
            dataCache.setMenu(parentMenu);
        }

        dataCache.getMessagesContainer().clearMessages();
    }

    public String[] getAvailableOptions() {
        List<String> result = new ArrayList<>();
        MonitoringDataCache dataCache = cacheComponent.getCacheBucket().getDataCache(MonitoringDataCache.class);
        UserPrivileges userPrivileges = dataCache.getUserPrivileges();
        for (PRIVILEGE privilege : userPrivileges.getPrivileges()) {
            if (privilege == PRIVILEGE.SERVER_INFO) {
                result.add(constants.getButtons().getMainMenuOptions().getServerInfo().getServerInfo());
            } else if (privilege == PRIVILEGE.APPS) {
                result.add(constants.getButtons().getMainMenuOptions().getApps().getApps());
            } else if (privilege == PRIVILEGE.PRIVILEGES) {
                result.add(constants.getButtons().getMainMenuOptions().getPrivileges().getPrivileges());
            } else if (privilege == PRIVILEGE.PMS) {
                result.add(constants.getButtons().getMainMenuOptions().getPms().getPms());
            } else if (privilege == PRIVILEGE.BOT_ACCESS) {
                result.add(constants.getButtons().getMainMenuOptions().getBotAccess().getBotAccess());
            }
        }

        return result.toArray(new String[0]);
    }
}
