package mavmi.telegram_bot.water_stuff.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.common.service.cache.ServiceCache;
import mavmi.telegram_bot.water_stuff.service.constants.Buttons;
import mavmi.telegram_bot.water_stuff.service.constants.Phrases;
import mavmi.telegram_bot.water_stuff.service.constants.Requests;
import mavmi.telegram_bot.water_stuff.service.data.DataException;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Service extends AbsService<UserCache> {

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
    private final HttpClient httpClient;
    private final Long pauseNotificationsTime;

    public Service(
            UsersWaterData usersWaterData,
            HttpClient httpClient,
            ServiceCache<UserCache> serviceCache,
            @Value("${service.pause-time}") Long pauseNotificationsTime
    ) {
        super(serviceCache);
        this.usersWaterData = usersWaterData;
        this.httpClient = httpClient;
        this.pauseNotificationsTime = pauseNotificationsTime;
    }

    public void handleRequest(BotRequestJson jsonDto) {
        long chatId = jsonDto.getChatId();
        String username = jsonDto.getUserJson().getUsername();
        String firstName = jsonDto.getUserJson().getFirstName();
        String lastName = jsonDto.getUserJson().getLastName();
        String msg = jsonDto.getUserMessageJson().getTextMessage();

        UserCache userCache = getUserCache(chatId, username, firstName, lastName);
        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return;
        }

        log.info("Got request. id: {}; username: {}; first name: {}; last name: {}; message: {}",
                userCache.getUserId(),
                userCache.getUsername(),
                userCache.getFirstName(),
                userCache.getLastName(),
                msg
        );

        IMenu userMenu = userCache.getMenu();
        IMenu userPremierMenu = userCache.getPremierMenu();

        if (msg.equals(Requests.CANCEL_REQ)) {
            cancelOperation(userCache);
        } else if (userPremierMenu == Menu.MAIN_MENU) {
            if (userMenu == Menu.ADD) {
                add_approve(userCache, msg);
            } else if (userMenu == Menu.ADD_APPROVE) {
                add_process(userCache, msg);
            } else if (userMenu == Menu.SELECT_GROUP) {
                getGroup_manageGroup(userCache, msg);
            } else {
                switch (msg) {
                    case (Requests.GET_FULL_INFO_REQ) -> getFullInfo(userCache);
                    case (Requests.GET_GROUP_REQ) -> getGroup_askForName(userCache);
                    case (Requests.ADD_GROUP_REQ) -> add_askForName(userCache);
                    default -> error(userCache);
                }
            }
        } else if (userPremierMenu == Menu.MANAGE_GROUP) {
            if (userMenu == Menu.EDIT_GROUP) {
                edit_process(userCache, msg);
            } else if (userMenu == Menu.RM) {
                rm_process(userCache, msg);
            } else {
                switch (msg) {
                    case (Buttons.INFO_BTN) -> getInfo(userCache);
                    case (Buttons.PAUSE_BTN) -> pause(userCache);
                    case (Buttons.CONTINUE_BTN) -> cont(userCache);
                    case (Buttons.WATER_BTN) -> water_process(userCache, false);
                    case (Buttons.FERTILIZE_BTN) -> water_process(userCache, true);
                    case (Buttons.EDIT_BTN) -> edit_askForData(userCache);
                    case (Buttons.RM_BTN) -> rm_approve(userCache);
                    case (Buttons.EXIT_BTN) -> exit(userCache);
                    default -> error(userCache);
                }
            }
        }
    }

    private void exit(UserCache user) {
        user.setPremierMenu(Menu.MAIN_MENU);
        user.setSelectedGroup(null);
        dropUserInfo(user);
        httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
    }

    private void getGroup_askForName(UserCache user) {
        if (usersWaterData.size(user.getUserId()) == 0) {
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            user.setMenu(Menu.SELECT_GROUP);
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.ENTER_GROUP_NAME_MSG,
                    getGroupsNames(user)
            );
        }
    }

    private void getGroup_manageGroup(UserCache user, String msg) {
        if (usersWaterData.get(user.getUserId(), msg) == null) {
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_MSG);
            dropUserInfo(user);
        } else {
            user.setPremierMenu(Menu.MANAGE_GROUP);
            user.setMenu(Menu.MANAGE_GROUP);
            user.setSelectedGroup(msg);
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.MANAGE_GROUP_MSG,
                    MANAGE_MENU_BUTTONS
            );
        }
    }

    private void add_askForName(UserCache user) {
        user.setMenu(Menu.ADD);
        httpClient.sendText(user.getUserId(), Phrases.ADD_GROUP_MSG);
    }

    private void add_approve(UserCache user, String msg) {
        user.setMenu(Menu.ADD_APPROVE);
        user.getLastMessages().add(msg);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.APPROVE_MSG,
                new String[]{ Buttons.YES_BTN, Buttons.NO_BTN }
        );
    }

    private void add_process(UserCache user, String msg) {
        if (!msg.equals(Buttons.YES_BTN)) {
            cancelOperation(user);
            return;
        }

        try {
            String[] splitted = user
                    .getLastMessages()
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

            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
        } catch (NumberFormatException | DataException e) {
            e.printStackTrace(System.out);
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
        } finally {
            dropUserInfo(user);
        }
    }

    private void rm_approve(UserCache user) {
        user.setMenu(Menu.RM);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.APPROVE_MSG,
                new String[]{ Buttons.YES_BTN, Buttons.NO_BTN }
        );
    }

    private void rm_process(UserCache user, String msg) {
        if (!msg.equals(Buttons.YES_BTN)) {
            cancelOperation(user);
        } else {
            usersWaterData.remove(user.getUserId(), user.getSelectedGroup());
            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
            user.setPremierMenu(Menu.MAIN_MENU);
            dropUserInfo(user);
        }
    }

    private void getInfo(UserCache user) {
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        httpClient.sendKeyboard(user.getUserId(), getReadableWaterInfo(waterInfo), MANAGE_MENU_BUTTONS);
    }

    private void getFullInfo(UserCache user) {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(user.getUserId());

        if (waterInfoList == null || waterInfoList.isEmpty()) {
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterInfo waterInfo : waterInfoList) {
                builder.append(getReadableWaterInfo(waterInfo)).append("\n\n");
            }

            httpClient.sendText(user.getUserId(), builder.toString());
        }
    }

    private void water_process(UserCache user, boolean fertilize) {
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        Date date = Date.valueOf(LocalDate.now());
        waterInfo.setWater(date);
        if (fertilize) {
            waterInfo.setFertilize(date);
        }
        usersWaterData.saveToFile();
        httpClient.sendKeyboard(user.getUserId(), Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
        dropUserInfo(user);
    }

    private void edit_askForData(UserCache user) {
        user.setMenu(Menu.EDIT_GROUP);
        httpClient.sendText(user.getUserId(), Phrases.ENTER_GROUP_DATA_MSG);
    }

    private void edit_process(UserCache user, String msg) {
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

            httpClient.sendKeyboard(user.getUserId(), Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
            httpClient.sendKeyboard(user.getUserId(), Phrases.INVALID_GROUP_NAME_FORMAT_MSG, MANAGE_MENU_BUTTONS);
        }

        dropUserInfo(user);
    }

    private void pause(UserCache user) {
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        waterInfo.setStopNotificationsUntil(System.currentTimeMillis() + pauseNotificationsTime);
        usersWaterData.saveToFile();
        httpClient.sendKeyboard(user.getUserId(), Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
    }

    private void cont(UserCache user) {
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getSelectedGroup());
        waterInfo.setStopNotificationsUntil(null);
        usersWaterData.saveToFile();
        httpClient.sendKeyboard(user.getUserId(), Phrases.SUCCESS_MSG, MANAGE_MENU_BUTTONS);
    }

    private void cancelOperation(UserCache user) {
        dropUserInfo(user);
        httpClient.sendText(user.getUserId(), Phrases.OPERATION_CANCELED_MSG);
    }

    private void error(UserCache user) {
        httpClient.sendText(user.getUserId(), Phrases.ERROR_MSG);
    }

    private void dropUserInfo(UserCache user) {
        user.setMenu(user.getPremierMenu());
        user.getLastMessages().clear();
    }

    private UserCache getUserCache(Long chatId, String username, String firstName, String lastName) {
        UserCache user = serviceCache.getUser(chatId);

        if (user == null) {
            user = new UserCache(chatId, Menu.MAIN_MENU, Menu.MAIN_MENU, username, firstName, lastName);
            serviceCache.putUser(user);
        }

        return user;
    }

    private String[] getGroupsNames(UserCache user) {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(user.getUserId());
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
}
