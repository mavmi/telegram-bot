package mavmi.telegram_bot.hb.service.serviceModule.fortune;

import mavmi.telegram_bot.common.database.model.HbPriseModel;
import mavmi.telegram_bot.common.database.model.HbScoreModel;
import mavmi.telegram_bot.common.database.repository.HbPriseRepository;
import mavmi.telegram_bot.common.database.repository.HbScoreRepository;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.HB_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.hb.cache.HbDataCache;
import mavmi.telegram_bot.hb.service.container.HbServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import mavmi.telegram_bot.hb.service.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class FortuneGetPriseIdServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public FortuneGetPriseIdServiceModule(
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
        HbPriseRepository priseRepository = commonServiceModule.getPriseRepository();
        HbScoreRepository scoreRepository = commonServiceModule.getScoreRepository();

        commonServiceModule.dropMenu();
        HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
        String peerUsername = dataCache.getHbSelectedUsername();
        String priseId = request.getMessageJson().getTextMessage();

        Optional<HbScoreModel> optionalHbScoreModel = scoreRepository.findByEduUsername(peerUsername);
        HbScoreModel scoreModel = optionalHbScoreModel.get();
        int count = (int) (scoreModel.getScore() / commonServiceModule.getFortuneCost());

        HbPriseModel priseModel = HbPriseModel
                .builder()
                .eduUsername(peerUsername)
                .priseId(priseId)
                .build();
        priseRepository.save(priseModel);

        List<HbPriseModel> allPrisesForUser = priseRepository.getAllByEduUsername(peerUsername);
        StringBuilder allPrisesIdx = new StringBuilder();
        for (int i = 0; i < allPrisesForUser.size(); i++) {
            allPrisesIdx.append(allPrisesForUser.get(i).getPriseId());
            if (i + 1 != allPrisesForUser.size()) {
                allPrisesIdx.append(", ");
            }
        }

        String outputMsg = "***" +
                commonServiceModule.getConstants().getPhrases().getSavePeerDataSuccess() +
                "*** " +
                "\n\n" +
                "***Пир: ***" +
                peerUsername +
                "\n" +
                "***Айдишник приза: ***" +
                priseId +
                "\n" +
                "***Столько раз еще можно крутануть колесо: ***" +
                count;

        if (allPrisesIdx.length() > 0) {
            outputMsg += "\n\n" +
                    "Список всех призов для " +
                    peerUsername +
                    ": " +
                    allPrisesIdx;
        }

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(outputMsg)
                .build();
        return HbServiceResponse
                .builder()
                .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }
}
