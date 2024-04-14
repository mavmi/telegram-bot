package mavmi.telegram_bot.water_stuff.service.service.water_stuff.serviceModule.common;

import lombok.Getter;
import mavmi.telegram_bot.common.cache.userData.inner.MenuContainer;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.WATER_STUFF_SERVICE_TASK;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.httpFilter.userSession.session.UserSession;
import mavmi.telegram_bot.common.service.menu.Menu;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.WaterStuffServiceConstantsHandler;
import mavmi.telegram_bot.water_stuff.service.constantsHandler.dto.WaterStuffServiceConstants;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.WaterStuffServiceMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Getter
@Component
public class CommonServiceModule {

    private final WaterStuffServiceConstants constants;
    private final UsersWaterData usersWaterData;
    private final Long pauseNotificationsTime;
    private final String[] manageMenuButtons;

    @Autowired
    private UserSession userSession;

    public CommonServiceModule(
            UsersWaterData usersWaterData,
            WaterStuffServiceConstantsHandler constantsHandler,
            @Value("${service.pause-time}") Long pauseNotificationsTime
    ) {
        this.constants = constantsHandler.get();
        this.usersWaterData = usersWaterData;
        this.pauseNotificationsTime = pauseNotificationsTime;
        this.manageMenuButtons = new String[] {
                constants.getButtons().getInfo(),
                constants.getButtons().getPause(),
                constants.getButtons().getDoContinue(),
                constants.getButtons().getWater(),
                constants.getButtons().getFertilize(),
                constants.getButtons().getEdit(),
                constants.getButtons().getRm(),
                constants.getButtons().getExit()
        };
    }

    public String getReadableWaterInfo(WaterInfo waterInfo) {
        StringBuilder builder = new StringBuilder();

        int i = 0;
        long EMPTY = -1;
        long[] daysDiff = new long[2];

        for (java.util.Date date : new java.util.Date[]{ waterInfo.getWater(), waterInfo.getFertilize() }) {
            daysDiff[i++] = (date == null) ?
                    EMPTY :
                    TimeUnit.DAYS.convert(
                            System.currentTimeMillis() - date.getTime(),
                            TimeUnit.MILLISECONDS
                    );
        }

        builder.append("***")
                .append("> ")
                .append(waterInfo.getName())
                .append("***")
                .append("\n")
                .append("Разница по дням: ")
                .append(waterInfo.getDiff())
                .append("\n")
                .append("Полив: ")
                .append(waterInfo.getWaterAsString());

        if (daysDiff[0] != EMPTY) {
            builder.append(" (дней прошло: ")
                    .append(daysDiff[0])
                    .append(")");
        }

        builder.append("\n")
                .append("Удобрение: ")
                .append(waterInfo.getFertilizeAsString());

        if (daysDiff[1] != EMPTY) {
            builder.append(" (дней прошло: ")
                    .append(daysDiff[1])
                    .append(")");
        }

        return builder.toString();
    }

    public String[] getGroupsNames() {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(userSession.getCache().getUserId());
        if (waterInfoList == null) {
            return new String[]{};
        }

        int size = waterInfoList.size();
        String[] arr = new String[size];

        int i = 0;
        for (WaterInfo waterInfo : waterInfoList) {
            arr[i++] = waterInfo.getName();
        }

        return arr;
    }

    public WaterStuffServiceRs cancel(WaterStuffServiceRq request) {
        userSession.getCache().getMessagesContainer().clearMessages();
        dropMenu();
        Menu menu = userSession.getCache().getMenuContainer().getLast();

        if (menu.equals(WaterStuffServiceMenu.MANAGE_GROUP)) {
            return createSendKeyboardResponse(
                    constants.getPhrases().getOperationCanceled(),
                    manageMenuButtons
            );
        } else {
            return createSendTextResponse(constants.getPhrases().getOperationCanceled());
        }
    }

    public WaterStuffServiceRs error(WaterStuffServiceRq request) {
        return createSendTextResponse(constants.getPhrases().getError());
    }

    public WaterStuffServiceRs createSendTextResponse(String msg) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.SEND_TEXT)
                .messageJson(messageJson)
                .build();
    }

    public WaterStuffServiceRs createSendKeyboardResponse(String msg, String[] keyboardButtons) {
        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(keyboardButtons)
                .build();

        return WaterStuffServiceRs
                .builder()
                .waterStuffServiceTask(WATER_STUFF_SERVICE_TASK.SEND_KEYBOARD)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();
    }

    public void dropMenu(Menu menuUntil) {
        MenuContainer menuContainer = userSession.getCache().getMenuContainer();
        Menu menu = menuContainer.getLast();

        while (!menuUntil.equals(menu)) {
            menuContainer.removeLast();
            menu = menuContainer.getLast();
        }
    }

    public void dropMenu() {
        MenuContainer menuContainer = userSession.getCache().getMenuContainer();
        WaterStuffServiceMenu menu = (WaterStuffServiceMenu) menuContainer.getLast();

        while (menu != null && !menu.isPremier()) {
            menuContainer.removeLast();
            menu = (WaterStuffServiceMenu) menuContainer.getLast();
        }
    }
}
