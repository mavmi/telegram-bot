package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.edit;

import lombok.SneakyThrows;
import mavmi.telegram_bot.common.service.dto.common.CallbackQueryJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CalendarServiceModule;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EditGroupFertilizeServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final CalendarServiceModule calendarServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public EditGroupFertilizeServiceModule(
            CommonServiceModule commonServiceModule,
            CalendarServiceModule calendarServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.calendarServiceModule = calendarServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(commonServiceModule.getConstants().getButtons().getChangeFertilize(), this::getCurrentMonthCalendar),
                this::onDefault
        );
    }

    @Override
    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = (messageJson == null) ? null : messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs getCurrentMonthCalendar(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_FERTILIZE);
        return commonServiceModule.createSendInlineKeyboardResponse(
                calendarServiceModule.getMonthYear(),
                calendarServiceModule.getMonthKeyboard(),
                null,
                false
        );
    }

    @SneakyThrows
    private WaterStuffServiceRs onDefault(WaterStuffServiceRq request) {
        WaterStuffServiceConstants constants = commonServiceModule.getConstants();
        CallbackQueryJson callbackQueryJson = request.getCallbackQueryJson();
        if (callbackQueryJson == null) {
            return commonServiceModule.createEmptyResponse();
        }

        Integer msgId = callbackQueryJson.getMessageId();
        String msg = callbackQueryJson.getData();

        if (calendarServiceModule.isDayFormat(msg)) {
            WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
            long fertilizeDate = CalendarServiceModule.DD_MM_YY_DATE_FORMAT.parse(msg).getTime();
            WaterStuffServiceRs response;

            if (fertilizeDate > System.currentTimeMillis()) {
                response = commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getInvalidDate(), commonServiceModule.getEditMenuButtons());
            } else {
                UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
                WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
                waterInfo.setFertilizeFromString(msg);
                usersWaterData.saveToFile();

                response = commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
            }

            dataCache.getMessagesContainer().clearMessages();
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
