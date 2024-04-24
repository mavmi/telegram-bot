package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.edit;

import lombok.SneakyThrows;
import mavmi.telegram_bot.common.dto.common.CallbackQueryJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CalendarServiceModule;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupFertilizeServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final CalendarServiceModule calendarServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupFertilizeServiceModule(
            CommonServiceModule commonServiceModule,
            CalendarServiceModule calendarServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.calendarServiceModule = calendarServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(constants.getButtons().getChangeFertilize(), this::getCurrentMonthCalendar),
                this::handleRequest
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = (messageJson == null) ? null : messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs getCurrentMonthCalendar(WaterStuffServiceRq request) {
        commonServiceModule.getUserSession().getCache().getMenuContainer().add(WaterStuffServiceMenu.EDIT_FERTILIZE);
        return commonServiceModule.createSendInlineKeyboardResponse(
                calendarServiceModule.getMonthYear(),
                calendarServiceModule.getMonthKeyboard(),
                null,
                false
        );
    }

    @SneakyThrows
    private WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        CallbackQueryJson callbackQueryJson = request.getCallbackQueryJson();
        if (callbackQueryJson == null) {
            return commonServiceModule.createEmptyResponse();
        }

        Integer msgId = callbackQueryJson.getMessageId();
        String msg = callbackQueryJson.getData();

        if (calendarServiceModule.isDayFormat(msg)) {
            WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
            long fertilizeDate = CalendarServiceModule.DD_MM_YY_DATE_FORMAT.parse(msg).getTime();
            WaterStuffServiceRs response;

            if (fertilizeDate > System.currentTimeMillis()) {
                response = commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getInvalidDate(), commonServiceModule.getEditMenuButtons());
            } else {
                UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
                WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
                waterInfo.setFertilizeFromString(msg);
                usersWaterData.saveToFile();

                response = commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
            }

            user.getMessagesContainer().clearMessages();
            commonServiceModule.dropMenu();

            return response;
        } else if (calendarServiceModule.isMonthFormat(msg)) {
            return commonServiceModule.createSendInlineKeyboardResponse(
                    calendarServiceModule.getMonthYear(msg),
                    calendarServiceModule.getMonthKeyboard(msg),
                    msgId,
                    true
            );
        } else {
            return commonServiceModule.createEmptyResponse();
        }
    }
}
