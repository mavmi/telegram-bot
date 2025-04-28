package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule;

import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExitServiceModule implements ServiceModule<RocketchatServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<RocketchatServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public ExitServiceModule(CommonServiceModule commonServiceModule) {
        this.commonServiceModule = commonServiceModule;
        this.serviceComponentsContainer.setDefaultServiceMethods(List.of(this::init, this::deleteIncomingMessage, this::onDefault));
    }

    @Override
    public void handleRequest(RocketchatServiceRq request) {
        for (ServiceMethod<RocketchatServiceRq> method : serviceComponentsContainer.getDefaultServiceMethods()) {
            method.process(request);
        }
    }

    private void init(RocketchatServiceRq request) {
        long activeCommandHash = Utils.calculateCommandHash(request.getMessageJson().getTextMessage(), System.currentTimeMillis());
        commonServiceModule.getUserCaches().getDataCache(RocketDataCache.class).setActiveCommandHash(activeCommandHash);
    }

    private void deleteIncomingMessage(RocketchatServiceRq request) {
        commonServiceModule.addMessageToDeleteAfterEnd(request.getMessageJson().getMsgId());
    }

    private void onDefault(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        commonServiceModule.getRocketchatRepository().deleteByTelegramId(chatId);
        int msgId = commonServiceModule.sendText(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getOk());
        commonServiceModule.deleteMessageAfterMillis(chatId, msgId, commonServiceModule.getDeleteAfterMillisNotification());
        commonServiceModule.deleteQueuedMessages(chatId);
    }
}
