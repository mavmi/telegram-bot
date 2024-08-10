package mavmi.telegram_bot.rocketchat.service.serviceModule;

import mavmi.telegram_bot.common.database.repository.RocketchatRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.rocketchat.constantsHandler.RocketchatServiceConstantsHandler;
import mavmi.telegram_bot.rocketchat.constantsHandler.dto.RocketchatServiceConstants;
import mavmi.telegram_bot.rocketchat.service.container.RocketchatServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class ExitServiceModule implements ServiceModule<RocketchatServiceRs, RocketchatServiceRq> {

    private final RocketchatRepository rocketchatRepository;
    private final RocketchatServiceConstants constants;
    private final RocketchatServiceMessageToServiceMethodContainer rocketchatServiceMessageToServiceMethodContainer;
    private final CommonServiceModule commonServiceModule;

    public ExitServiceModule(
        RocketchatRepository rocketchatRepository,
        RocketchatServiceConstantsHandler constantsHandler,
        CommonServiceModule commonServiceModule
    ) {
        this.rocketchatRepository = rocketchatRepository;
        this.constants = constantsHandler.get();
        this.rocketchatServiceMessageToServiceMethodContainer = new RocketchatServiceMessageToServiceMethodContainer(
                this::onDefault
        );
        this.commonServiceModule = commonServiceModule;
    }

    @Override
    public RocketchatServiceRs handleRequest(RocketchatServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = messageJson.getTextMessage();
        ServiceMethod<RocketchatServiceRs, RocketchatServiceRq> method = rocketchatServiceMessageToServiceMethodContainer.getMethod(msg);
        return method.process(request);
    }

    private RocketchatServiceRs onDefault(RocketchatServiceRq request) {
        long chatId = request.getChatId();
        rocketchatRepository.deleteByTelegramId(chatId);
        return commonServiceModule.createSendTextResponse(constants.getPhrases().getOk());
    }
}
