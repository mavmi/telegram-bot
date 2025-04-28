package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.pms;

import mavmi.parameters_management_system.common.parameter.impl.Parameter;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.cache.inner.dataCache.PmsManagement;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.menu.MonitoringServiceMenu;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class PmsNewValueServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public PmsNewValueServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), commonServiceModule::exit)
                .add(commonServiceModule.getConstants().getButtons().getPms().getInfo(), this::onInfo)
                .setDefaultServiceMethod(this::onDefault);
    }

    @VerifyPrivilege(PRIVILEGE.PMS)
    public void init(MonitoringServiceRq request) {
        commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).setMenu(MonitoringServiceMenu.PMS_EDIT);
        commonServiceModule.sendCurrentMenuButtons(request.getChatId());
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
        String newValue = request.getMessageJson().getTextMessage();
        PmsManagement pmsManagement = commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).getPmsManagement();
        Parameter parameter = pmsManagement.getParameter();
        Parameter updatedParameter = Parameter.builder()
                .name(parameter.getName())
                .type(parameter.getType())
                .value(newValue)
                .build();

        if (commonServiceModule.getRemoteParameterPlugin().updateParameter(updatedParameter)) {
            pmsManagement.setParameter(updatedParameter);
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getOk());
        } else {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getError());
        }
    }

    private void onInfo(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        Parameter parameter = commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).getPmsManagement().getParameter();

        StringBuilder builder = new StringBuilder();
        builder.append("Name: ")
                .append(parameter.getName())
                .append("\n")
                .append("Type: ")
                .append(parameter.getType().name())
                .append("\n")
                .append("Value: ")
                .append(parameter.getValue());

        commonServiceModule.sendCurrentMenuButtons(chatId, builder.toString());
    }
}
