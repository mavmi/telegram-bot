package mavmi.telegram_bot.hb.service.serviceModule.score;

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

import java.util.Map;
import java.util.Optional;

@Component
public class ScoreServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public ScoreServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getScore(), this::onScoreRequest
                ),
                this::onDefault
        );
    }

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<HbServiceResponse, HbServiceRequest> method = container.getMethod(msg);
        return method.process(request);
    }

    private HbServiceResponse onScoreRequest(HbServiceRequest request) {
        HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
        dataCache.getMenuContainer().add(HbServiceMenu.SCORE);

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(commonServiceModule.getConstants().getPhrases().getAskForPeerUsername())
                .build();
        return HbServiceResponse
                .builder()
                .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    private HbServiceResponse onDefault(HbServiceRequest request) {
        String peerUsername = request.getMessageJson().getTextMessage().toLowerCase();
        HbScoreRepository scoreRepository = commonServiceModule.getScoreRepository();
        Optional<HbScoreModel> optionalHbScoreModel = scoreRepository.findByEduUsername(peerUsername);
        String msg;

        if (optionalHbScoreModel.isPresent()) {
            commonServiceModule.dropMenu();
            HbScoreModel scoreModel = optionalHbScoreModel.get();

            int count = (int) (scoreModel.getScore() / commonServiceModule.getFortuneCost());
            msg = commonServiceModule.getConstants().getPhrases().getFortuneCount() + count;
        } else {
            msg = commonServiceModule.getConstants().getPhrases().getFortuneCount() + 0;
        }

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();
        return HbServiceResponse
                .builder()
                .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }
}
