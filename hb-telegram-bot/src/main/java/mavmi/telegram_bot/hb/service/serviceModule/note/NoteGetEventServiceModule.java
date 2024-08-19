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
import mavmi.telegram_bot.hb.sheets.dto.Events;
import org.springframework.stereotype.Component;

@Component
public class NoteGetEventServiceModule implements ServiceModule<HbServiceResponse, HbServiceRequest> {

    private final CommonServiceModule commonServiceModule;
    private final HbServiceMessageToServiceMethodContainer container;

    public NoteGetEventServiceModule(
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
        String eventName = request.getMessageJson().getTextMessage();
        if (eventName.equals(commonServiceModule.getConstants().getButtons().getMenuBack())) {
            commonServiceModule.previousMenu();

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
        }

        Events events = commonServiceModule.getGoogleSheetsHandler().getAllEvents();

        if (events.getEventByName(eventName) == null) {
            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getInvalidEventName())
                    .build();
            ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                    .builder()
                    .keyboardButtons(commonServiceModule.prepareEventsKeyboardButtons(events.getEventsByGroupName(dataCache.getHbSelectedEventGroup())))
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_KEYBOARD)
                    .messageJson(messageJson)
                    .replyKeyboardJson(replyKeyboardJson)
                    .build();
        } else {
            dataCache.getMenuContainer().add(HbServiceMenu.NOTE_GRADE);
            dataCache.setHbSelectedEvent(eventName);

            MessageJson messageJson = MessageJson
                    .builder()
                    .textMessage(commonServiceModule.getConstants().getPhrases().getAskForGrade())
                    .build();
            ReplyKeyboardJson replyKeyboardJson = ReplyKeyboardJson
                    .builder()
                    .keyboardButtons(commonServiceModule.prepareGradesKeyboard())
                    .build();
            return HbServiceResponse
                    .builder()
                    .hbServiceTask(HB_SERVICE_TASK.SEND_KEYBOARD)
                    .messageJson(messageJson)
                    .replyKeyboardJson(replyKeyboardJson)
                    .build();
        }
    }
}
