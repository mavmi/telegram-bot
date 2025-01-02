package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.edit;

import lombok.SneakyThrows;
import mavmi.telegram_bot.common.service.dto.common.CallbackQueryJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.common.service.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterConstants;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CalendarServiceModule;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

@Component
public class EditGroupWaterServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final CalendarServiceModule calendarServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    public EditGroupWaterServiceModule(
            CommonServiceModule commonServiceModule,
            CalendarServiceModule calendarServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.calendarServiceModule = calendarServiceModule;
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getChangeWater(), this::getCurrentMonthCalendar)
                .setDefaultServiceMethod(this::onDefault);
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        String msg = (messageJson == null) ? null : messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void getCurrentMonthCalendar(WaterStuffServiceRq request) {
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class).getMenuContainer().add(WaterStuffServiceMenu.EDIT_WATER);
        commonServiceModule.sendInlineKeyboard(
                request.getChatId(),
                calendarServiceModule.getMonthYear(),
                calendarServiceModule.getMonthKeyboard(),
                null,
                false
        );
    }

    @SneakyThrows
    private void onDefault(WaterStuffServiceRq request) {
        WaterConstants constants = commonServiceModule.getConstants();
        CallbackQueryJson callbackQueryJson = request.getCallbackQueryJson();
        if (callbackQueryJson == null) {
            return;
        }

        Integer msgId = callbackQueryJson.getMessageId();
        String msg = callbackQueryJson.getData();

        if (calendarServiceModule.isDayFormat(msg)) {
            WaterDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterDataCache.class);
            long waterDate = CalendarServiceModule.DD_MM_YY_DATE_FORMAT.parse(msg).getTime();

            if (waterDate > System.currentTimeMillis()) {
                dataCache.getMessagesContainer().clearMessages();
                commonServiceModule.dropUserMenu();
                commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getInvalidDate(), commonServiceModule.getEditMenuButtons());
            } else {
                UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
                WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
                waterInfo.setWaterFromString(msg);
                usersWaterData.saveToFile();

                dataCache.getMessagesContainer().clearMessages();
                commonServiceModule.dropUserMenu();
                commonServiceModule.sendReplyKeyboard(request.getChatId(), constants.getPhrases().getSuccess(), commonServiceModule.getEditMenuButtons());
            }
        } else if (calendarServiceModule.isMonthFormat(msg)) {
            commonServiceModule.sendInlineKeyboard(
                    request.getChatId(),
                    calendarServiceModule.getMonthYear(msg),
                    calendarServiceModule.getMonthKeyboard(msg),
                    msgId,
                    true
            );
        }
    }
}
