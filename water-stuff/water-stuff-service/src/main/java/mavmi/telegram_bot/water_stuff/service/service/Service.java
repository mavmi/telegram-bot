package mavmi.telegram_bot.water_stuff.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BotNames;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.service.cache.ServiceCache;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.water_stuff.service.constants.Buttons;
import mavmi.telegram_bot.water_stuff.service.constants.Phrases;
import mavmi.telegram_bot.water_stuff.service.constants.Requests;
import mavmi.telegram_bot.water_stuff.service.data.DataException;
import mavmi.telegram_bot.water_stuff.service.data.pause.UsersPauseNotificationsData;
import mavmi.telegram_bot.water_stuff.service.data.water.UsersWaterData;
import mavmi.telegram_bot.water_stuff.service.data.water.WaterInfo;
import mavmi.telegram_bot.water_stuff.service.http.HttpClient;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class Service extends AbsService<UserCache> {

    private final UserAuthentication userAuthentication;
    private final UsersWaterData usersWaterData;
    private final UsersPauseNotificationsData usersPauseNotificationsData;
    private final HttpClient httpClient;

    public Service(
            UserAuthentication userAuthentication,
            UsersWaterData usersWaterData,
            UsersPauseNotificationsData usersPauseNotificationsData,
            HttpClient httpClient,
            ServiceCache<UserCache> serviceCache
    ) {
        super(serviceCache);
        this.userAuthentication = userAuthentication;
        this.usersWaterData = usersWaterData;
        this.usersPauseNotificationsData = usersPauseNotificationsData;
        this.httpClient = httpClient;
    }

    public void handleRequest(BotRequestJson jsonDto) {
        long chatId = jsonDto.getChatId();
        String username = jsonDto.getUserJson().getUsername();
        String firstName = jsonDto.getUserJson().getFirstName();
        String lastName = jsonDto.getUserJson().getLastName();
        String msg = jsonDto.getUserMessageJson().getTextMessage();

        UserCache userCache = getUserCache(chatId, username, firstName, lastName);
        if (!userCache.getIsPrivilegeGranted()) {
            log.error("Access denied! id: {}", chatId);
            return;
        }
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
        if (msg.equals(Requests.CANCEL_REQ)) {
            cancelOperation(userCache);
        } else if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case (Requests.ADD_GROUP_REQ) -> add_askForName(userCache);
                case (Requests.RM_GROUP_REQ) -> rm_askForName(userCache);
                case (Requests.GET_INFO_REQ) -> getInfo(userCache);
                case (Requests.WATER_REQ) -> water_askForName(userCache, false);
                case (Requests.FERTILIZE_REQ) -> water_askForName(userCache, true);
                case (Requests.EDIT_GROUP_REQ) -> edit_askForName(userCache);
                case (Requests.PAUSE_REQ) -> pause(userCache);
                case (Requests.CONTINUE_REQ) -> cont(userCache);
                default -> error(userCache);
            }
        } else if (userMenu == Menu.ADD) {
            add_approve(userCache, msg);
        } else if (userMenu == Menu.ADD_APPROVE) {
            add_process(userCache, msg);
        } else if (userMenu == Menu.RM) {
            rm_approve(userCache, msg);
        } else if (userMenu == Menu.RM_APPROVE) {
            rm_process(userCache, msg);
        } else if (userMenu == Menu.WATER || userMenu == Menu.FERTILIZE) {
            water_process(userCache, msg);
        } else if (userMenu == Menu.EDIT_GROUP_1) {
            edit_askForData(userCache, msg);
        } else if (userMenu == Menu.EDIT_GROUP_2) {
            edit_process(userCache, msg);
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

    private void rm_askForName(UserCache user) {
        if (usersWaterData.size(user.getUserId()) == 0) {
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            user.setMenu(Menu.RM);
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.ENTER_GROUP_NAME_MSG,
                    getGroupsNames(user)
            );
        }
    }

    private void rm_approve(UserCache user, String msg) {
        if (usersWaterData.get(user.getUserId(), msg) == null) {
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_MSG);
            dropUserInfo(user);
        } else {
            user.setMenu(Menu.RM_APPROVE);
            user.getLastMessages().add(msg);
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.APPROVE_MSG,
                    new String[]{ Buttons.YES_BTN, Buttons.NO_BTN }
            );
        }
    }

    private void rm_process(UserCache user, String msg) {
        if (!msg.equals(Buttons.YES_BTN)) {
            cancelOperation(user);
        } else {
            usersWaterData.remove(user.getUserId(), user.getLastMessages().get(0));
            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
            dropUserInfo(user);
        }
    }

    private void getInfo(UserCache user) {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(user.getUserId());

        if (waterInfoList == null || waterInfoList.isEmpty()) {
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterInfo waterInfo : waterInfoList) {
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

                builder.append("\n\n");
            }

            httpClient.sendText(user.getUserId(), builder.toString()/*, ParseMode.Markdown*/);
        }
    }

    private void water_askForName(UserCache user, boolean fertilize) {
        if (usersWaterData.size(user.getUserId()) == 0){
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            user.setMenu((fertilize) ? Menu.FERTILIZE : Menu.WATER);
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.ENTER_GROUP_NAME_MSG,
                    getGroupsNames(user)
            );
        }
    }

    private void water_process(UserCache user, String msg) {
        WaterInfo waterInfo = usersWaterData.get(user.getUserId(), msg);
        if (waterInfo == null) {
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_MSG);
        } else {
            boolean fertilize = user.getMenu() == Menu.FERTILIZE;
            Date date = Date.valueOf(LocalDate.now());
            waterInfo.setWater(date);
            if (fertilize) {
                waterInfo.setFertilize(date);
            }
            usersWaterData.saveToFile();
            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
        }

        dropUserInfo(user);
    }

    private void edit_askForName(UserCache user) {
        if (usersWaterData.size(user.getUserId()) == 0){
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            user.setMenu(Menu.EDIT_GROUP_1);
            httpClient.sendKeyboard(
                    user.getUserId(),
                    Phrases.ENTER_GROUP_NAME_MSG,
                    getGroupsNames(user)
            );
        }
    }

    private void edit_askForData(UserCache user, String msg) {
        if (usersWaterData.get(user.getUserId(), msg) == null) {
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_MSG);
            dropUserInfo(user);
        } else {
            user.setMenu(Menu.EDIT_GROUP_2);
            user.getLastMessages().add(msg);
            httpClient.sendText(user.getUserId(), Phrases.ENTER_GROUP_DATA_MSG);
        }
    }

    private void edit_process(UserCache user, String msg) {
        String[] splitted = msg.split("\n");

        try {
            if (splitted.length != 4) {
                throw new RuntimeException(Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
            }

            WaterInfo waterInfo = usersWaterData.get(user.getUserId(), user.getLastMessages().get(0));
            if (waterInfo == null) {
                httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_MSG);
                return;
            }

            waterInfo.setName(splitted[0]);
            waterInfo.setDiff(Integer.parseInt(splitted[1]));
            waterInfo.setWaterFromString(splitted[2]);
            waterInfo.setFertilizeFromString(splitted[3]);
            usersWaterData.saveToFile();

            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
        } catch (RuntimeException e) {
            e.printStackTrace(System.out);
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
        }

        dropUserInfo(user);
    }

    private void pause(UserCache user) {
        usersPauseNotificationsData.put(
                user.getUserId(),
                System.currentTimeMillis() + usersPauseNotificationsData.getPauseTime()
        );
        httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
    }

    private void cont(UserCache user) {
        usersPauseNotificationsData.remove(user.getUserId());
        httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
    }

    private void cancelOperation(UserCache user) {
        dropUserInfo(user);
        httpClient.sendText(user.getUserId(), Phrases.OPERATION_CANCELED_MSG);
    }

    private void error(UserCache user) {
        httpClient.sendText(user.getUserId(), Phrases.ERROR_MSG);
    }

    private void dropUserInfo(UserCache user) {
        user.setMenu(Menu.MAIN_MENU);
        user.getLastMessages().clear();
    }

    private UserCache getUserCache(Long chatId, String username, String firstName, String lastName) {
        UserCache user = serviceCache.getUser(chatId);

        if (user == null) {
            Boolean isPrivilegeGranted = userAuthentication.isPrivilegeGranted(chatId, BotNames.WATER_STUFF_BOT);
            user = new UserCache(chatId, Menu.MAIN_MENU, username, firstName, lastName, isPrivilegeGranted);
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
}
