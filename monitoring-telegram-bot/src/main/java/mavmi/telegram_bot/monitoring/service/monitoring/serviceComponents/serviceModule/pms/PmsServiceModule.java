package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.pms;

import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class PmsServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final PmsNewValueServiceModule pmsNewValueServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public PmsServiceModule(
            CommonServiceModule commonServiceModule,
            PmsNewValueServiceModule pmsNewValueServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.pmsNewValueServiceModule = pmsNewValueServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getMainMenuOptions().getPms().getPms(), this::init)
                .setDefaultServiceMethod(this::onDefault);
    }

    @Override
    @VerifyPrivilege(PRIVILEGE.PMS)
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void onDefault(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        String parameterName = request.getMessageJson().getTextMessage();
        Parameter parameter = commonServiceModule.getRemoteParameterPlugin().getParameter(parameterName);

        if (parameter == null) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getError());
        } else {
            commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).getPmsManagement().setParameter(parameter);
            pmsNewValueServiceModule.init(request);
        }
    }

    private void init(MonitoringServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.PMS);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
    }
}
