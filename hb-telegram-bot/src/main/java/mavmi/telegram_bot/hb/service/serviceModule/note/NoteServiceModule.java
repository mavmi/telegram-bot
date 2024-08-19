package mavmi.telegram_bot.hb.service.serviceModule.note;

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

@Component
public class NoteServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public NoteServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(
                Map.of(
                        commonServiceModule.getConstants().getRequests().getNote(), this::onNoteRequest
                )
        );
    }

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<HbServiceResponse, HbServiceRequest> method = container.getMethod(msg);
        return method.process(request);
    }

    private HbServiceResponse onNoteRequest(HbServiceRequest request) {
        if (!commonServiceModule.checkAccess(request.getChatId())) {
            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getNotAuthorized())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        }

        HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
        dataCache.getMenuContainer().add(HbServiceMenu.NOTE_USERNAME);

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
}
