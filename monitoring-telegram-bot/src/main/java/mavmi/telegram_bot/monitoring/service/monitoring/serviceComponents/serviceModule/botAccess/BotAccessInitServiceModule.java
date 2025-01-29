package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.botAccess;

import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.monitoring.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BotAccessInitServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final BotAccessServiceModule botAccessServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public BotAccessInitServiceModule(
            CommonServiceModule commonServiceModule,
            BotAccessServiceModule botAccessServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.botAccessServiceModule = botAccessServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getBotAccess().getBotAccess(), this::init)
                .setDefaultServiceMethod(this::onDefault);
    }

    @Override
    @VerifyPrivilege(PRIVILEGE.BOT_ACCESS)
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void onDefault(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        long chatIdToInspect = Utils.parseTelegramId(request.getMessageJson().getTextMessage());
        if (chatIdToInspect == -1) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getBotAccess().getInvalidId());
        } else {
            MonitoringDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class);
            RuleRepository ruleRepository = commonServiceModule.getRuleRepository();
            Optional<RuleModel> optional = ruleRepository.findById(chatIdToInspect);
            dataCache.getBotAccessManagement()
                            .setWorkingTelegramId(chatIdToInspect)
                            .setWaterStuff((optional.isEmpty()) ? false : optional.get().getWaterStuff())
                            .setMonitoring((optional.isEmpty()) ? false : optional.get().getMonitoring());
            botAccessServiceModule.initMenuLevel(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.BOT_ACCESS_INIT);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId(), commonServiceModule.getConstants().getPhrases().getBotAccess().getAskForUserId());
    }
}
