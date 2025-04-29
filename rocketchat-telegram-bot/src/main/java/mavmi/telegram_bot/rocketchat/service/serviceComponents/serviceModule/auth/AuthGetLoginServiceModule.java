package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.menu.RocketMenu;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthGetLoginServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @PostConstruct
    public void setup() {
        this.serviceComponentsContainer.setDefaultServiceMethods(List.of(this::getLogin, this::deleteIncomingMessage));
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getDefaultServiceMethods()) {
            method.process(request);
        }
    }

    private void getLogin(RocketchatServiceRq request) {
        RocketDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(RocketDataCache.class);
        dataCache.getCreds().setRocketchatUsername(request.getMessageJson().getTextMessage());
        dataCache.setMenu(RocketMenu.AUTH_ENTER_PASSWORD);

        int msgId = commonServiceModule.sendText(request.getChatId(), commonServiceModule.getConstants().getPhrases().getAuth().getEnterPassword());
        commonServiceModule.addMessageToDeleteAfterEnd(msgId);
    }

    public void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }
}
