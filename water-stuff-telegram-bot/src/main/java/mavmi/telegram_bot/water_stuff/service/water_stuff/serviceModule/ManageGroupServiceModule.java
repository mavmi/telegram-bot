package mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.water_stuff.cache.WaterStuffServiceDataCache;
import mavmi.telegram_bot.water_stuff.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.water_stuff.container.WaterStuffServiceMessageToServiceMethodContainer;
import mavmi.telegram_bot.water_stuff.service.water_stuff.serviceModule.common.CommonServiceModule;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRq;
import mavmi.telegram_bot.water_stuff.service.dto.waterStuffService.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.direct.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.direct.ServiceModule;
import mavmi.telegram_bot.water_stuff.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.water_stuff.menu.WaterStuffServiceMenu;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Map;

@Component
public class ManageGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final WaterStuffServiceConstants constants;
    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToServiceMethodContainer waterStuffServiceMessageToHandlerContainer;

    public ManageGroupServiceModule(
            CommonServiceModule commonServiceModule,
            EditGroupServiceModule editGroupServiceModule,
            RemoveGroupServiceModule removeGroupServiceModule,
            PauseNotificationsServiceModule pauseNotificationsServiceModule,
            WaterStuffServiceConstantsHandler constantsHandler
    ) {
        this.constants = constantsHandler.get();
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToServiceMethodContainer(
                Map.of(
                        constants.getButtons().getEdit(), editGroupServiceModule::handleRequest,
                        constants.getButtons().getRm(), removeGroupServiceModule::handleRequest,
                        constants.getButtons().getInfo(), this::getInfo,
                        constants.getButtons().getPause(), pauseNotificationsServiceModule::handleRequest,
                        constants.getButtons().getDoContinue(), this::continueNotifications,
                        constants.getButtons().getWater(), this::water,
                        constants.getButtons().getFertilize(), this::fertilize,
                        constants.getButtons().getExit(), this::exit
                ),
                commonServiceModule::error
        );
    }

    @Override
    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq request) {
        MessageJson messageJson = request.getMessageJson();
        if (messageJson == null) {
            return commonServiceModule.createEmptyResponse();
        }
        String msg = messageJson.getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs getInfo(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
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

        return commonServiceModule.createSendReplyKeyboardResponse(res, commonServiceModule.getManageMenuButtons());
    }

    private WaterStuffServiceRs continueNotifications(WaterStuffServiceRq request) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());

        waterInfo.setStopNotificationsUntil(null);
        usersWaterData.saveToFile();
        return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
    }

    private WaterStuffServiceRs water(WaterStuffServiceRq request) {
        return waterProcess(false);
    }

    private WaterStuffServiceRs fertilize(WaterStuffServiceRq request) {
        return waterProcess(true);
    }

    private WaterStuffServiceRs exit(WaterStuffServiceRq request) {
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MAIN_MENU);
        commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class).getMessagesContainer().clearMessages();

        return commonServiceModule.createSendTextResponse(constants.getPhrases().getSuccess());
    }

    private WaterStuffServiceRs waterProcess(boolean fertilize) {
        WaterStuffServiceDataCache dataCache = commonServiceModule.getCacheComponent().getCacheBucket().getDataCache(WaterStuffServiceDataCache.class);
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(dataCache.getUserId(), dataCache.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());

        waterInfo.setWater(date);
        if (fertilize) {
            waterInfo.setFertilize(date);
        }
        usersWaterData.saveToFile();

        return commonServiceModule.createSendReplyKeyboardResponse(constants.getPhrases().getSuccess(), commonServiceModule.getManageMenuButtons());
    }
}
