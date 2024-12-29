package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth;

import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthGetPasswordServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final AuthServiceModule authServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public AuthGetPasswordServiceModule(
            CommonServiceModule commonServiceModule,
            AuthServiceModule authServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.authServiceModule = authServiceModule;
        this.serviceComponentsContainer.setDefaultServiceMethods(List.of(this::getPassword, this::deleteIncomingMessage));
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getDefaultServiceMethods()) {
            method.process(request);
        }
    }

    private void getPassword(RocketchatServiceRq request) {
        String password = request.getMessageJson().getTextMessage();
        RocketDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketDataCache.class);

        dataCache.getCreds().setPassword(password);
        dataCache.getCreds().setPasswordHash(Utils.calculateHash(password));

        authServiceModule.doLogin(request);
        commonServiceModule.dropMenu();
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMsgToDeleteAfterEnd(request.getMessageJson().getMsgId());
        commonServiceModule.deleteMsgs(request.getChatId());
    }
}
