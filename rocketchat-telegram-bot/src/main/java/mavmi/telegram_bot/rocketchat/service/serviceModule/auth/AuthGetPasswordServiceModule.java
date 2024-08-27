package mavmi.telegram_bot.rocketchat.service.serviceModule.auth;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.cache.RocketchatServiceDataCache;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AuthGetPasswordServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;
    private final AuthServiceModule authServiceModule;

    public AuthGetPasswordServiceModule(
            CommonServiceModule commonServiceModule,
            AuthServiceModule authServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.authServiceModule = authServiceModule;
        this.rocketchatChainServiceMessageToServiceSecondaryMethodsContainer = new RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(List.of(this::getPassword, this::deletePassword));
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServiceSecondaryMethodsContainer.getMethods(msg);
    }

    private RocketchatServiceRs getPassword(RocketchatServiceRq request) {
        RocketchatServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(RocketchatServiceDataCache.class);
        dataCache.getCreds().setPassword(request.getMessageJson().getTextMessage());

        return commonServiceModule.createResponse(
                authServiceModule.doLogin(request).getTextMessage(),
                null,
                null,
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT)
        );
    }

    private RocketchatServiceRs deletePassword(RocketchatServiceRq request) {
        return commonServiceModule.createResponse(
                null,
                null,
                request.getMessageJson().getMsgId(),
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_END, ROCKETCHAT_SERVICE_TASK.END)
        );
    }
}
