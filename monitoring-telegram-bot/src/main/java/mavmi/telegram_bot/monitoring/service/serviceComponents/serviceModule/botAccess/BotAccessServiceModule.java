package mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.botAccess;

import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.BotAccessManagement;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class BotAccessServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public BotAccessServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getBotAccess().getInfo(), this::onInfo)
                .add(commonServiceModule.getConstants().getButtons().getBotAccess().getAddWaterStuff(), this::addWaterStuff)
                .add(commonServiceModule.getConstants().getButtons().getBotAccess().getRevokeWaterStuff(), this::revokeWaterStuff)
                .add(commonServiceModule.getConstants().getButtons().getBotAccess().getAddMonitoring(), this::addMonitoring)
                .add(commonServiceModule.getConstants().getButtons().getBotAccess().getRevokeMonitoring(), this::revokeMonitoring)
                .setDefaultServiceMethod(this::onDefault);
    }

    @VerifyPrivilege(PRIVILEGE.BOT_ACCESS)
    public void initMenuLevel(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.BOT_ACCESS);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
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
        commonServiceModule.sendCurrentMenuButtons(chatId);
    }

    private void onInfo(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        BotAccessManagement botAccessManagement = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getBotAccessManagement();
        StringBuilder builder = new StringBuilder();

        builder.append("User id: ")
                .append(botAccessManagement.getWorkingTelegramId())
                .append("\n")
                .append("\n")
                .append("Water stuff: ")
                .append(botAccessManagement.isWaterStuff())
                .append("\n")
                .append("Monitoring: ")
                .append(botAccessManagement.isMonitoring());

        commonServiceModule.sendCurrentMenuButtons(chatId, builder.toString());
    }

    private void addWaterStuff(MonitoringServiceRq request) {
        updateAccess(request, BOT_NAME.WATER_STUFF_BOT, true);
    }

    private void revokeWaterStuff(MonitoringServiceRq request) {
        updateAccess(request, BOT_NAME.WATER_STUFF_BOT, false);
    }

    private void addMonitoring(MonitoringServiceRq request) {
        updateAccess(request, BOT_NAME.MONITORING_BOT, true);
    }

    private void revokeMonitoring(MonitoringServiceRq request) {
        updateAccess(request, BOT_NAME.MONITORING_BOT, false);
    }

    private void updateAccess(MonitoringServiceRq request, BOT_NAME botName, boolean accessGranted) {
        long chatId = request.getChatId();
        BotAccessManagement botAccessManagement = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getBotAccessManagement();
        RuleRepository ruleRepository = commonServiceModule.getRuleRepository();

        if (botName == BOT_NAME.WATER_STUFF_BOT) {
            botAccessManagement.setWaterStuff(accessGranted);
        } else if (botName == BOT_NAME.MONITORING_BOT) {
            botAccessManagement.setMonitoring(accessGranted);
        }

        RuleModel model = RuleModel.builder()
                .userid(botAccessManagement.getWorkingTelegramId())
                .waterStuff(botAccessManagement.isWaterStuff())
                .monitoring(botAccessManagement.isMonitoring())
                .build();

        ruleRepository.save(model);
        commonServiceModule.sendCurrentMenuButtons(chatId);
    }
}
