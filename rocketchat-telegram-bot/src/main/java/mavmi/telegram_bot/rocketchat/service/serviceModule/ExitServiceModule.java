package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.service.serviceModule.chained.ChainedServiceModule;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExitServiceModule implements ChainedServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatChainServiceMessageToServiceSecondaryMethodsContainer rocketchatChainServiceMessageToServiceSecondaryMethodsContainer;
    private final CommonServiceModule commonServiceModule;

    public ExitServiceModule(
        CommonServiceModule commonServiceModule
    ) {
        List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methodsOnDefault = List.of(this::deleteIncomingMessage, this::onDefault);

        this.rocketchatChainServiceMessageToServiceSecondaryMethodsContainer = new RocketchatChainServiceMessageToServiceSecondaryMethodsContainer(
                methodsOnDefault
        );
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> prepareMethodsChain(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        return rocketchatChainServiceMessageToServiceSecondaryMethodsContainer.getMethods(msg);
    }

    private RocketchatServiceRs onDefault(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        commonServiceModule.getRocketchatRepository().deleteByTelegramId(chatId);

        return commonServiceModule.createResponse(commonServiceModule.getConstants().getPhrases().getOk(), null, null, commonServiceModule.getDeleteAfterMillis(), List.of(ROCKETCHAT_SERVICE_TASK.SEND_TEXT, ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_TIME_MILLIS, ROCKETCHAT_SERVICE_TASK.END));
    }

    private RocketchatServiceRs deleteIncomingMessage(RocketchatServiceRq request) {
        return commonServiceModule.createResponse(
                null,
                null,
                request.getMessageJson().getMsgId(),
                null,
                List.of(ROCKETCHAT_SERVICE_TASK.DELETE_AFTER_END)
        );
    }
}
