package mavmi.telegram_bot.hb.service.serviceModule.fortune;

import mavmi.telegram_bot.common.database.model.HbScoreModel;
import mavmi.telegram_bot.common.database.repository.HbScoreRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.HB_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.hb.cache.HbDataCache;
import mavmi.telegram_bot.hb.service.container.HbServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import mavmi.telegram_bot.hb.service.menu.HbServiceMenu;
import mavmi.telegram_bot.hb.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FortuneGetUsernameServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public FortuneGetUsernameServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(
                this::onDefault
        );
    }

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<HbServiceResponse, HbServiceRequest> method = container.getMethod(msg);
        return method.process(request);
    }

    private HbServiceResponse onDefault(HbServiceRequest request) {
        String peerUsername = request.getMessageJson().getTextMessage().toLowerCase();
        HbScoreRepository scoreRepository = commonServiceModule.getScoreRepository();

        Optional<HbScoreModel> hbScoreModelOptional = scoreRepository.findByEduUsername(peerUsername);
        if (hbScoreModelOptional.isEmpty()) {
            commonServiceModule.dropMenu();

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getNotEnoughCoins())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        }

        HbScoreModel model = hbScoreModelOptional.get();
        if (model.getScore() < commonServiceModule.getFortuneCost()) {
            commonServiceModule.dropMenu();

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getNotEnoughCoins())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        } else {
            scoreRepository.updateScoreAndFortuneByUsername(peerUsername, model.getScore() - commonServiceModule.getFortuneCost(), model.getFortune() + 1);
            HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
            dataCache.setHbSelectedUsername(peerUsername);
            dataCache.getMenuContainer().add(HbServiceMenu.FORTUNE_PRISE_ID);

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getAskForPriseId())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        }
    }
}
