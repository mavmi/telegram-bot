package mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.privileges;

import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;
import mavmi.telegram_bot.lib.database_starter.model.PrivilegesModel;
import mavmi.telegram_bot.lib.database_starter.repository.PrivilegesRepository;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.monitoring.aop.privilege.api.VerifyPrivilege;
import mavmi.telegram_bot.monitoring.cache.MonitoringDataCache;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.monitoring.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PrivilegesInfoServiceModule implements ServiceModule<MonitoringServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<MonitoringServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public PrivilegesInfoServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        serviceComponentsContainer.setDefaultServiceMethod(this::onDefault);
    }

    @Override
    @VerifyPrivilege(PRIVILEGE.PRIVILEGES)
    public void handleRequest(MonitoringServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<MonitoringServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void onDefault(MonitoringServiceRq request) {
        long chatId = request.getChatId();
        long chatIdToInspect = commonServiceModule.getUserCaches().getDataCache(MonitoringDataCache.class).getPrivilegesManagement().getWorkingTelegramId();

        PrivilegesRepository privilegesRepository = commonServiceModule.getPrivilegesRepository();
        Optional<PrivilegesModel> optional = privilegesRepository.findById(chatIdToInspect);

        if (optional.isEmpty()) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getPrivileges().getUserNotFound());
            return;
        }

        PrivilegesModel model = optional.get();
        List<PRIVILEGE> privileges = model.getPrivileges();
        if (privileges.isEmpty()) {
            commonServiceModule.sendCurrentMenuButtons(chatId, commonServiceModule.getConstants().getPhrases().getPrivileges().getNoPrivileges());
        } else {
            StringBuilder builder = new StringBuilder()
                    .append("User id: ")
                    .append(chatIdToInspect)
                    .append("\n")
                    .append("\n")
                    .append("Privileges:")
                    .append("\n");
            for (PRIVILEGE privilege : privileges) {
                builder.append(privilege.getName())
                        .append("\n");
            }

            commonServiceModule.sendCurrentMenuButtons(chatId, builder.toString());
        }
    }
}
