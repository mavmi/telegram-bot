package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule;

import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.service.method.ServiceMethod;
import mavmi.telegram_bot.common.service.serviceModule.ServiceModule;
import mavmi.telegram_bot.water_stuff.service.cache.WaterStuffServiceUserDataCache;
import mavmi.telegram_bot.water_stuff.service.constants.Buttons;
import mavmi.telegram_bot.water_stuff.service.constants.Phrases;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.container.WaterStuffServiceMessageToHandlerContainer;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common.CommonServiceModule;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Map;

@Component
public class ManageGroupServiceModule implements ServiceModule<WaterStuffServiceRs, WaterStuffServiceRq> {

    private final CommonServiceModule commonServiceModule;
    private final WaterStuffServiceMessageToHandlerContainer waterStuffServiceMessageToHandlerContainer;

    public ManageGroupServiceModule(
            CommonServiceModule commonServiceModule,
            EditGroupServiceModule editGroupServiceModule,
            RemoveGroupServiceModule removeGroupServiceModule
    ) {
        this.commonServiceModule = commonServiceModule;
        this.waterStuffServiceMessageToHandlerContainer = new WaterStuffServiceMessageToHandlerContainer(
                Map.of(
                        Buttons.EDIT_BTN, editGroupServiceModule::process,
                        Buttons.RM_BTN, removeGroupServiceModule::process,
                        Buttons.INFO_BTN, this::getInfo,
                        Buttons.PAUSE_BTN, this::pauseNotifications,
                        Buttons.CONTINUE_BTN, this::continueNotifications,
                        Buttons.WATER_BTN, this::water,
                        Buttons.FERTILIZE_BTN, this::fertilize,
                        Buttons.EXIT_BTN, this::exit

                ),
                commonServiceModule::error
        );
    }

    @Override
    public WaterStuffServiceRs process(WaterStuffServiceRq request) {
        String msg = request.getMessageJson().getTextMessage();
        ServiceMethod<WaterStuffServiceRs, WaterStuffServiceRq> method = waterStuffServiceMessageToHandlerContainer.getMethod(msg);
        return method.process(request);
    }

    private WaterStuffServiceRs getInfo(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        WaterInfo waterInfo = commonServiceModule.getUsersWaterData().get(user.getUserId(), user.getSelectedGroup());
        Long stopNotificationsUntil = waterInfo.getStopNotificationsUntil();
        String res = commonServiceModule.getReadableWaterInfo(waterInfo);

        if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateTimeStr = simpleDateFormat.format(new java.util.Date(stopNotificationsUntil));
            res += "\n\n" +
                    "Обновления возобновятся " +
                    dateTimeStr;
        }

        return commonServiceModule.createSendKeyboardResponse(res, CommonServiceModule.MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs pauseNotifications(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());

        waterInfo.setStopNotificationsUntil(System.currentTimeMillis() + commonServiceModule.getPauseNotificationsTime());
        usersWaterData.saveToFile();
        return commonServiceModule.createSendKeyboardResponse(Phrases.SUCCESS_MSG, CommonServiceModule.MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs continueNotifications(WaterStuffServiceRq request) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());

        waterInfo.setStopNotificationsUntil(null);
        usersWaterData.saveToFile();
        return commonServiceModule.createSendKeyboardResponse(Phrases.SUCCESS_MSG, CommonServiceModule.MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs water(WaterStuffServiceRq request) {
        return waterProcess(false);
    }

    private WaterStuffServiceRs fertilize(WaterStuffServiceRq request) {
        return waterProcess(true);
    }

    private WaterStuffServiceRs exit(WaterStuffServiceRq request) {
        commonServiceModule.dropMenu(WaterStuffServiceMenu.MAIN_MENU);
        commonServiceModule.getUserSession().getCache().getMessagesContainer().clearMessages();

        return commonServiceModule.createSendTextResponse(Phrases.SUCCESS_MSG);
    }

    private WaterStuffServiceRs waterProcess(boolean fertilize) {
        WaterStuffServiceUserDataCache user = commonServiceModule.getUserSession().getCache();
        UsersWaterData usersWaterData = commonServiceModule.getUsersWaterData();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());

        waterInfo.setWater(date);
        if (fertilize) {
            waterInfo.setFertilize(date);
        }
        usersWaterData.saveToFile();

        return commonServiceModule.createSendKeyboardResponse(Phrases.SUCCESS_MSG, CommonServiceModule.MANAGE_MENU_BUTTONS);
    }
}
