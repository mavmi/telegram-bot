package mavmi.telegram_bot.water_stuff.service.service;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BotNames;
import mavmi.telegram_bot.common.database.auth.UserAuthentication;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.service.AbsService;
import mavmi.telegram_bot.common.utils.service.IMenu;
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
public class Service extends AbsService {

    private final UserAuthentication userAuthentication;
    private final UsersWaterData usersWaterData;
    private final UsersPauseNotificationsData usersPauseNotificationsData;
    private final HttpClient httpClient;

    public Service(
            UserAuthentication userAuthentication,
            UsersWaterData usersWaterData,
            UsersPauseNotificationsData usersPauseNotificationsData,
            HttpClient httpClient
    ) {
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

        ServiceUser user = getUser(chatId, username, firstName, lastName);

        log.info("New request. id: {}; username: {}; first name: {}; last name: {}; message: {}", chatId, username, firstName, lastName, msg);
        if (!userAuthentication.isPrivilegeGranted(chatId, BotNames.WATER_STUFF_BOT)) {
            log.error("Access denied! id: {}", chatId);
            return;
        }
        if (msg == null) {
            log.error("Message is NULL! id: {}", chatId);
            return;
        }

        IMenu userMenu = user.getMenu();
        if (msg.equals(Requests.CANCEL_REQ)) {
            cancelOperation(user);
        } else if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case (Requests.ADD_GROUP_REQ) -> add_askForName(user);
                case (Requests.RM_GROUP_REQ) -> rm_askForName(user);
                case (Requests.GET_INFO_REQ) -> getInfo(user);
                case (Requests.WATER_REQ) -> water_askForName(user, false);
                case (Requests.FERTILIZE_REQ) -> water_askForName(user, true);
                case (Requests.EDIT_GROUP_REQ) -> edit_askForName(user);
                case (Requests.PAUSE_REQ) -> pause(user);
                case (Requests.CONTINUE_REQ) -> cont(user);
                default -> error(user);
            }
        } else if (userMenu == Menu.ADD) {
            add_approve(user, msg);
        } else if (userMenu == Menu.ADD_APPROVE) {
            add_process(user, msg);
        } else if (userMenu == Menu.RM) {
            rm_approve(user, msg);
        } else if (userMenu == Menu.RM_APPROVE) {
            rm_process(user, msg);
        } else if (userMenu == Menu.WATER || userMenu == Menu.FERTILIZE) {
            water_process(user, msg);
        } else if (userMenu == Menu.EDIT_GROUP_1) {
            edit_askForData(user, msg);
        } else if (userMenu == Menu.EDIT_GROUP_2) {
            edit_process(user, msg);
        }
    }

    private void add_askForName(ServiceUser user) {
        user.setMenu(Menu.ADD);
        httpClient.sendText(user.getUserId(), Phrases.ADD_GROUP_MSG);
    }

    private void add_approve(ServiceUser user, String msg) {
        user.setMenu(Menu.ADD_APPROVE);
        user.getLastMessages().add(msg);
        httpClient.sendKeyboard(
                user.getUserId(),
                Phrases.APPROVE_MSG,
                new String[]{ Buttons.YES_BTN, Buttons.NO_BTN }
        );
    }

    private void add_process(ServiceUser user, String msg) {
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
            waterInfo.setWater(WaterInfo.NULL_STR);
            waterInfo.setFertilize(WaterInfo.NULL_STR);
            usersWaterData.put(user.getUserId(), waterInfo);

            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
        } catch (NumberFormatException | DataException e) {
            e.printStackTrace(System.err);
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
        } finally {
            dropUserInfo(user);
        }
    }

    private void rm_askForName(ServiceUser user) {
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

    private void rm_approve(ServiceUser user, String msg) {
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

    private void rm_process(ServiceUser user, String msg) {
        if (!msg.equals(Buttons.YES_BTN)) {
            cancelOperation(user);
        } else {
            usersWaterData.remove(user.getUserId(), user.getLastMessages().get(0));
            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
            dropUserInfo(user);
        }
    }

    private void getInfo(ServiceUser user) {
        List<WaterInfo> waterInfoList = usersWaterData.getAll(user.getUserId());

        if (waterInfoList == null || waterInfoList.isEmpty()) {
            httpClient.sendText(user.getUserId(), Phrases.ON_EMPTY_MSG);
        } else {
            StringBuilder builder = new StringBuilder();

            for (WaterInfo waterInfo : waterInfoList) {
                int i = 0;
                long EMPTY = -1;
                long[] daysDiff = new long[2];
                for (java.util.Date date : new java.util.Date[]{ waterInfo.getWaterAsDate(), waterInfo.getFertilizeAsDate() }) {
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

    private void water_askForName(ServiceUser user, boolean fertilize) {
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

    private void water_process(ServiceUser user, String msg) {
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

    private void edit_askForName(ServiceUser user) {
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

    private void edit_askForData(ServiceUser user, String msg) {
        if (usersWaterData.get(user.getUserId(), msg) == null) {
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_MSG);
            dropUserInfo(user);
        } else {
            user.setMenu(Menu.EDIT_GROUP_2);
            user.getLastMessages().add(msg);
            httpClient.sendText(user.getUserId(), Phrases.ENTER_GROUP_DATA_MSG);
        }
    }

    private void edit_process(ServiceUser user, String msg) {
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
            waterInfo.setWater(splitted[2]);
            waterInfo.setFertilize(splitted[3]);
            usersWaterData.saveToFile();

            httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
            httpClient.sendText(user.getUserId(), Phrases.INVALID_GROUP_NAME_FORMAT_MSG);
        }

        dropUserInfo(user);
    }

    private void pause(ServiceUser user) {
        usersPauseNotificationsData.put(
                user.getUserId(),
                System.currentTimeMillis() + usersPauseNotificationsData.getPauseTime()
        );
        httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
    }

    private void cont(ServiceUser user) {
        usersPauseNotificationsData.remove(user.getUserId());
        httpClient.sendText(user.getUserId(), Phrases.SUCCESS_MSG);
    }

    private void cancelOperation(ServiceUser user) {
        dropUserInfo(user);
        httpClient.sendText(user.getUserId(), Phrases.OPERATION_CANCELED_MSG);
    }

    private void error(ServiceUser user) {
        httpClient.sendText(user.getUserId(), Phrases.ERROR_MSG);
    }

    private void dropUserInfo(ServiceUser user) {
        user.setMenu(Menu.MAIN_MENU);
        user.getLastMessages().clear();
    }

    private ServiceUser getUser(Long chatId, String username, String firstName, String lastName) {
        ServiceUser user = (ServiceUser) idToUser.get(chatId);

        if (user == null) {
            user = new ServiceUser(chatId, Menu.MAIN_MENU, username, firstName, lastName);
            idToUser.put(chatId, user);
        }

        return user;
    }

    private String[] getGroupsNames(ServiceUser user) {
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
