package mavmi.telegram_bot.hb.service.serviceModule.note;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.ReplyKeyboardJson;
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

@Component
public class NoteGetPeerUsernameServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public NoteGetPeerUsernameServiceModule(
            CommonServiceModule commonServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.container = new HbServiceMessageToServiceMethodContainer(this::onDefault);
    }

    @Override
    public HbServiceResponse handleRequest(HbServiceRequest request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<HbServiceResponse, HbServiceRequest> method = container.getMethod(msg);
        return method.process(request);
    }

    private HbServiceResponse onDefault(HbServiceRequest request) {
        HbDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(HbDataCache.class);
        String peerUsername = request.getMessageJson().getTextMessage();

        if (checkInput(peerUsername) && commonServiceModule.getPeerVerifier().verifyPeerName(peerUsername)) {
            dataCache.getMenuContainer().add(HbServiceMenu.NOTE_EVENT_GROUPS);
            dataCache.setHbSelectedUsername(peerUsername);

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getAskForEventGroupName())
                    .build();
            ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                    .builder()
                    .keyboardButtons(commonServiceModule.prepareEventGroupsKeyboardButtons())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_KEYBOARD)
                    .messageJson(messageJson)
                    .replyKeyboardJson(replyKeyboardJson)
                    .build();
        } else {
            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidPeerUsername())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_TEXT)
                    .messageJson(messageJson)
                    .build();
        }
    }

    private boolean checkInput(String str) {
        if (str.length() < 4 || str.length() > 8) {
            return false;
        }

        for (Character c : str.toCharArray()) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }

        return true;
    }
}
