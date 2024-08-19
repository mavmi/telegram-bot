package mavmi.telegram_bot.hb.service.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.HB_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.hb.service.container.HbServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import mavmi.telegram_bot.hb.service.serviceModule.auth.AuthServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.fortune.FortuneServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.note.NoteServiceModule;
import mavmi.telegram_bot.hb.service.serviceModule.score.ScoreServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MainMenuServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final AuthServiceModule authServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public MainMenuServiceModule(
            CommonServiceModule commonServiceModule,
            AuthServiceModule authServiceModule,
            NoteServiceModule noteServiceModule,
            ScoreServiceModule scoreServiceModule,
            FortuneServiceModule fortuneServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.authServiceModule = authServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getAuth(), authServiceModule::onAuthRequest,
                        commonServiceModule.getConstants().getRequests().getStart(), authServiceModule::onAuthRequest,
                        commonServiceModule.getConstants().getRequests().getNote(), noteServiceModule::handleRequest,
                        commonServiceModule.getConstants().getRequests().getScore(), scoreServiceModule::handleRequest,
                        commonServiceModule.getConstants().getRequests().getFortune(), fortuneServiceModule::handleRequest
                ),
                this::onInvalidRequest
        );
    }

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<HbServiceResponse, HbServiceRequest> method = container.getMethod(msg);
        return method.process(request);
    }

    public HbServiceResponse onInvalidRequest(HbServiceRequest request) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidRequest())
                .build();
        return HbServiceResponse
                .builder()
                .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }
}
