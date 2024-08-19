package mavmi.telegram_bot.hb.service.serviceModule.auth;

import mavmi.telegram_bot.common.database.model.HbAccessGrantedModel;
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
public class AuthServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public AuthServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getAuth(), this::onAuthRequest,
                        commonServiceModule.getConstants().getRequests().getStart(), this::onAuthRequest
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

    public HbServiceResponse onAuthRequest(HbServiceRequest request) {
        long chatId = request.getChatId();
        Optional<HbAccessGrantedModel> opt = commonServiceModule.getAccessRepository().findByTelegramId(chatId);
        HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
        String msg;

        if (opt.isEmpty() || opt.get().getAccessGranted() == null || !opt.get().getAccessGranted()) {
            msg = commonServiceModule.getConstants().getPhrases().getAskForPassword();
            dataCache.getMenuContainer().add(HbServiceMenu.AUTH);
        } else {
            msg = commonServiceModule.getConstants().getPhrases().getAlreadyLoggedIn();
            commonServiceModule.dropMenu();
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

    public HbServiceResponse onDefault(HbServiceRequest request) {
        commonServiceModule.dropMenu();
        String password = request.getMessageJson().getTextMessage();

        if (password.equals(commonServiceModule.getServicePassword())) {
            HbAccessGrantedModel hbAccessGrantedModel = HbAccessGrantedModel
                    .builder()
                    .telegramId(request.getChatId())
                    .accessGranted(true)
                    .build();
            commonServiceModule
                    .getAccessRepository()
                    .save(hbAccessGrantedModel);

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getAuthSuccess())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        } else {
            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidPassword())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        }
    }
}
