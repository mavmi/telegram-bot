package mavmi.telegram_bot.water_stuff.service.service.water_stuff;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.userData.AbstractUserDataCache;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.tasks.WATER_STUFF_SERVICE_TASK;
import mavmi.telegram_bot.common.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.common.service.AbstractService;
import mavmi.telegram_bot.common.service.menu.IMenu;
import mavmi.telegram_bot.water_stuff.service.cache.UserDataCache;
import mavmi.telegram_bot.water_stuff.service.constants.Buttons;
import mavmi.telegram_bot.water_stuff.service.constants.Phrases;
import mavmi.telegram_bot.water_stuff.service.constants.Requests;
import mavmi.telegram_bot.water_stuff.service.data.DataException;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.menu.Menu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WhaterStuffService extends AbstractService {

    private static final String[] MANAGE_MENU_BUTTONS = new String[] {
            Buttons.INFO_BTN,
            Buttons.PAUSE_BTN,
            Buttons.CONTINUE_BTN,
            Buttons.WATER_BTN,
            Buttons.FERTILIZE_BTN,
            Buttons.EDIT_BTN,
            Buttons.RM_BTN,
            Buttons.EXIT_BTN
    };

    private final UsersWaterData usersWaterData;
    private final Long pauseNotificationsTime;

    @Autowired
    private UserSession userSession;

    public WhaterStuffService(
            UsersWaterData usersWaterData,
            @Value("${service.pause-time}") Long pauseNotificationsTime
    ) {
        this.usersWaterData = usersWaterData;
        this.pauseNotificationsTime = pauseNotificationsTime;
    }

    public WaterStuffServiceRs handleRequest(WaterStuffServiceRq jsonDto) {
        long chatId = jsonDto.getChatId();
        String msg = jsonDto.getMessageJson().getTextMessage();

        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return error();
        }

        UserDataCache userCache = userSession.getCache();
        log.info("Got request. id: {}; username: {}, first name: {}; last name: {}, message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );

        IMenu userMenu = userCache.getMenu();
        IMenu userPremierMenu = userCache.getPremierMenu();

        if (msg.equals(Requests.CANCEL_REQ)) {
            return cancelOperation();
        } else if (userPremierMenu == Menu.MAIN_MENU) {
            if (userMenu == Menu.ADD) {
                return add_approve(msg);
            } else if (userMenu == Menu.ADD_APPROVE) {
                return add_process(msg);
            } else if (userMenu == Menu.SELECT_GROUP) {
                return getGroup_manageGroup(msg);
            } else {
                return switch (msg) {
                    case (Requests.GET_FULL_INFO_REQ) -> getFullInfo();
                    case (Requests.GET_GROUP_REQ) -> getGroup_askForName();
                    case (Requests.ADD_GROUP_REQ) -> add_askForName();
                    default -> error();
                };
            }
        } else if (userPremierMenu == Menu.MANAGE_GROUP) {
            if (userMenu == Menu.EDIT_GROUP) {
                return edit_process(msg);
            } else if (userMenu == Menu.RM) {
                return rm_process(msg);
            } else {
                return switch (msg) {
                    case (Buttons.INFO_BTN) -> getInfo();
                    case (Buttons.PAUSE_BTN) -> pause();
                    case (Buttons.CONTINUE_BTN) -> cont();
                    case (Buttons.WATER_BTN) -> water_process(false);
                    case (Buttons.FERTILIZE_BTN) -> water_process(true);
                    case (Buttons.EDIT_BTN) -> edit_askForData();
                    case (Buttons.RM_BTN) -> rm_approve();
                    case (Buttons.EXIT_BTN) -> exit();
                    default -> error();
                };
            }
        } else {
            return error();
        }
    }

    @Override
    public AbstractUserDataCache initCache() {
        return new UserDataCache(userSession.getId(), Menu.MAIN_MENU, Menu.MAIN_MENU);
    }

    private WaterStuffServiceRs exit() {
        UserDataCache user = userSession.getCache();
        user.setPremierMenu(Menu.MAIN_MENU);
        user.setSelectedGroup(null);
        dropUserInfo();
        return createSendTextResponse(Phrases.SUCCESS_MSG);
    }

    private WaterStuffServiceRs getGroup_askForName() {
        UserDataCache user = userSession.getCache();

        if (usersWaterData.size(user.getUserId()) == 0) {
            return createSendTextResponse(Phrases.ON_EMPTY_MSG);
        } else {
            user.setMenu(Menu.SELECT_GROUP);
            return createSendKeyboardResponse(Phrases.ENTER_GROUP_NAME_MSG, getGroupsNames());
        }
    }

    private WaterStuffServiceRs getGroup_manageGroup(String msg) {
        UserDataCache user = userSession.getCache();

        if (usersWaterData.get(user.getUserId(), msg) == null) {
            dropUserInfo();
            return createSendTextResponse(Phrases.INVALID_GROUP_NAME_MSG);
        } else {
            user.setPremierMenu(Menu.MANAGE_GROUP);
            user.setMenu(Menu.MANAGE_GROUP);
            user.setSelectedGroup(msg);
            return createSendKeyboardResponse(Phrases.MANAGE_GROUP_MSG, MANAGE_MENU_BUTTONS);
        }
    }

    private WaterStuffServiceRs add_askForName() {
        userSession.getCache().setMenu(Menu.ADD);
        return createSendTextResponse(Phrases.ADD_GROUP_MSG);
    }

    private WaterStuffServiceRs add_approve(String msg) {
        UserDataCache user = userSession.getCache();
        user.setMenu(Menu.ADD_APPROVE);
        user.getMessagesHistory().add(msg);
        return createSendKeyboardResponse(Phrases.APPROVE_MSG, new String[]{ Buttons.YES_BTN, Buttons.NO_BTN });
    }

    private WaterStuffServiceRs add_process(String msg) {
        if (!msg.equals(Buttons.YES_BTN)) {
            return cancelOperation();
        }

        UserDataCache user = userSession.getCache();
        try {
            String[] splitted = user
                    .getMessagesHistory()
                    .get(0)
                    .replaceAll(" ", "").split(";");
            if (splitted.length != 2) {
                throw new NumberFormatException();
            }

            String name = splitted[0];
            int diff = Integer.parseInt(splitted[1]);

            WaterInfo waterInfo = new WaterInfo();
            waterInfo.setUserId(user.getUserId());
            waterInfo.setName(name);
            waterInfo.setDiff(diff);
            waterInfo.setWaterFromString(WaterInfo.NULL_STR);
            waterInfo.setFertilizeFromString(WaterInfo.NULL_STR);
            usersWaterData.put(user.getUserId(), waterInfo);

            return createSendTextResponse(Phrases.SUCCESS_MSG);
        } catch (NumberFormatException | DataException e) {
            e.printStackTrace(System.out);
            return createSendTextResponse(Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
        } finally {
            dropUserInfo();
        }
    }

    private WaterStuffServiceRs rm_approve() {
        userSession.getCache().setMenu(Menu.RM);
        return createSendKeyboardResponse(Phrases.APPROVE_MSG, new String[]{ Buttons.YES_BTN, Buttons.NO_BTN });
    }

    private WaterStuffServiceRs rm_process(String msg) {
        UserDataCache user = userSession.getCache();

        if (!msg.equals(Buttons.YES_BTN)) {
            return cancelOperation();
        } else {
            usersWaterData.remove(user.getUserId(), user.getSelectedGroup());
            user.setPremierMenu(Menu.MAIN_MENU);
            dropUserInfo();
            return createSendTextResponse(Phrases.SUCCESS_MSG);
        }
    }

    private WaterStuffServiceRs getInfo() {
        UserDataCache user = userSession.getCache();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        Long stopNotificationsUntil = waterInfo.getStopNotificationsUntil();
        String res = getReadableWaterInfo(waterInfo);

        if (stopNotificationsUntil != null && stopNotificationsUntil > System.currentTimeMillis()) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
            String dateTimeStr = simpleDateFormat.format(new java.util.Date(stopNotificationsUntil));
            res += "\n\n" +
                    "Обновления возобновятся " +
                    dateTimeStr;
        }

        return createSendKeyboardResponse(res, MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs getFullInfo() {
        UserDataCache user = userSession.getCache();
        List<WaterInfo> waterInfoList = usersWaterData.getAll(user.getUserId());

        if (waterInfoList == null || waterInfoList.isEmpty()) {
            return createSendTextResponse(Phrases.ON_EMPTY_MSG);
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterInfo waterInfo : waterInfoList) {
                builder.append(getReadableWaterInfo(waterInfo)).append("\n\n");
            }

            return createSendTextResponse(builder.toString());
        }
    }

    private WaterStuffServiceRs water_process(boolean fertilize) {
        UserDataCache user = userSession.getCache();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());
        waterInfo.setWater(date);
        if (fertilize) {
            waterInfo.setFertilize(date);
        }
        usersWaterData.saveToFile();
        dropUserInfo();
        return createSendKeyboardResponse(Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs edit_askForData() {
        userSession.getCache().setMenu(Menu.EDIT_GROUP);
        return createSendTextResponse(Phrases.ENTER_GROUP_DATA_MSG);
    }

    private WaterStuffServiceRs edit_process(String msg) {
        UserDataCache user = userSession.getCache();
        String[] splitted = msg.split("\n");

        try {
            if (splitted.length != 4) {
                throw new RuntimeException(Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
            }

            WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());

            waterInfo.setName(splitted[0]);
            waterInfo.setDiff(Integer.parseInt(splitted[1]));
            waterInfo.setWaterFromString(splitted[2]);
            waterInfo.setFertilizeFromString(splitted[3]);
            usersWaterData.saveToFile();

            return createSendKeyboardResponse(Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
            return createSendKeyboardResponse(Phrases.INVALID_GROUP_NAME_FORMAT_MSG, MANAGE_MENU_BUTTONS);
        } finally {
            dropUserInfo();
        }
    }

    private WaterStuffServiceRs pause() {
        UserDataCache user = userSession.getCache();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        waterInfo.setStopNotificationsUntil(System.currentTimeMillis() + pauseNotificationsTime);
        usersWaterData.saveToFile();
        return createSendKeyboardResponse(Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs cont() {
        UserDataCache user = userSession.getCache();
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        waterInfo.setStopNotificationsUntil(null);
        usersWaterData.saveToFile();
        return createSendKeyboardResponse(Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
    }

    private WaterStuffServiceRs cancelOperation() {
        dropUserInfo();
        return createSendTextResponse(Phrases.OPERATION_CANCELED_MSG);
    }

    private WaterStuffServiceRs error() {
        return createSendTextResponse(Phrases.ERROR_MSG);
    }

    private void dropUserInfo() {
        UserDataCache user = userSession.getCache();
        user.setMenu(user.getPremierMenu());
        user.getMessagesHistory().clear();
    }

    private String[] getGroupsNames() {
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

    private String getReadableWaterInfo(WaterInfo waterInfo) {
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

    private WaterStuffServiceRs createSendTextResponse(String msg) {
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

    private WaterStuffServiceRs createSendKeyboardResponse(String msg, String[] keyboardButtons) {
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
}
