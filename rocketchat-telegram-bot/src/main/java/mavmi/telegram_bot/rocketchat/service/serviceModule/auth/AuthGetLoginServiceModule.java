package mavmi.telegram_bot.rocketchat.service.serviceModule.auth;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.menu.RocketchatServiceMenu;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthGetLoginServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;

    public AuthGetLoginServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.rocketchatChainServiceMessageToServiceSecondaryMethodsContainer = new RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(List.of(this::getLogin, this::deleteIncomingMessage));
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServiceSecondaryMethodsContainer.getMethods(msg);
    }

    private RocketchatServiceRs getLogin(RocketchatServiceRq request) {
        RocketchatServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketchatServiceDataCache.class);
        dataCache.getCreds().setUsername(request.getMessageJson().getTextMessage());
        dataCache.getMenuContainer().add(RocketchatServiceMenu.AUTH_ENTER_PASSWORD);

        return commonServiceModule.createResponse(
                commonServiceModule.getConstants().getPhrases().getEnterPassword(),
                null,
                null,
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_END)
        );
    }

    public RocketchatServiceRs deleteIncomingMessage(RocketchatServiceRq request) {
        return commonServiceModule.createResponse(
                null,
                null,
                request.getMessageJson().getMsgId(),
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_END)
        );
    }
}
