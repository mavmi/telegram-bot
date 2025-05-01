package mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.service_api.serviceComponents.container.ServiceComponentsContainer;
import mavmi.telegram_bot.lib.service_api.serviceComponents.method.ServiceMethod;
import mavmi.telegram_bot.lib.service_api.serviceComponents.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.cache.WaterDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.waterStuff.serviceModule.common.CommonServiceModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ManageGroupServiceModule implements ServiceModule<WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final ServiceComponentsContainer<WaterStuffServiceRq> serviceComponentsContainer = new ServiceComponentsContainer<>();

    @Autowired
    public void setup(EditGroupServiceModule editGroupServiceModule,
                      RemoveGroupServiceModule removeGroupServiceModule,
                      PauseNotificationsServiceModule pauseNotificationsServiceModule) {
        this.serviceComponentsContainer.add(commonServiceModule.getConstants().getButtons().getManageGroup().getEdit(), editGroupServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getManageGroup().getRm(), removeGroupServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getManageGroup().getInfo(), this::getInfo)
                .add(commonServiceModule.getConstants().getButtons().getManageGroup().getPause(), pauseNotificationsServiceModule::handleRequest)
                .add(commonServiceModule.getConstants().getButtons().getManageGroup().getDoContinue(), this::continueNotifications)
                .add(commonServiceModule.getConstants().getButtons().getManageGroup().getWater(), this::water)
                .add(commonServiceModule.getConstants().getButtons().getManageGroup().getFertilize(), this::fertilize)
                .add(commonServiceModule.getConstants().getButtons().getCommon().getExit(), this::exit)
                .setDefaultServiceMethod(commonServiceModule::error);
    }

    @Override
    public void handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return;
        }

        String msg = messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRq> method = serviceComponentsContainer.getMethod(msg);
        method.process(request);
    }

    private void getInfo(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class);
        WaterInfo waterInfo = commonServiceModule.getUsersWaterData().get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Long stopNotificationsUntil = waterInfo.getStopNotificationsUntil();
        String res = commonServiceModule.getReadableWaterInfo(waterInfo);

        if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateTimeStr = simpleDateFormat.format(new java.util.Date(stopNotificationsUntil));
            res += "\n\n" +
                    "Обновления возобновятся " +
                    dateTimeStr;
        }

        commonServiceModule.sendReplyKeyboard(request.getChatId(), res, commonServiceModule.getManageMenuButtons());
    }

    private void continueNotifications(WaterStuffServiceRq request) {
        WaterDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());

        waterInfo.setStopNotificationsUntil(null);
        usersWaterData.saveToFile();
        commonServiceModule.sendReplyKeyboard(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getSuccess(), commonServiceModule.getManageMenuButtons());
    }

    private void water(WaterStuffServiceRq request) {
        waterProcess(request.getChatId(), false);
    }

    private void fertilize(WaterStuffServiceRq request) {
        waterProcess(request.getChatId(), true);
    }

    private void exit(WaterStuffServiceRq request) {
        commonServiceModule.dropUserMenu(WaterStuffServiceMenu.MAIN_MENU);
        commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class).getMessagesContainer().clearMessages();
        commonServiceModule.sendTextDeleteKeyboard(request.getChatId(), commonServiceModule.getConstants().getPhrases().getCommon().getSuccess());
    }

    private void waterProcess(long chatId, boolean fertilize) {
        WaterDataCache dataCache = commonServiceModule.getUserCaches().getDataCache(WaterDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());

        waterInfo.setWater(date);
        if (fertilize) {
            waterInfo.setFertilize(date);
        }
        usersWaterData.saveToFile();

        commonServiceModule.sendReplyKeyboard(chatId, commonServiceModule.getConstants().getPhrases().getCommon().getSuccess(), commonServiceModule.getManageMenuButtons());
    }
}
