package mavmi.telegram_bot.water_stuff_bot.service;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.auth.BotNames;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.common.service.AbsService;
import mavmi.telegram_bot.common.service.IMenu;
import mavmi.telegram_bot.water_stuff_bot.data.DataException;
import mavmi.telegram_bot.water_stuff_bot.data.WaterContainer;
import mavmi.telegram_bot.water_stuff_bot.data.WaterInfo;
import mavmi.telegram_bot.water_stuff_bot.telegram_bot.Bot;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Map;

import static mavmi.telegram_bot.water_stuff_bot.constants.Buttons.YES_BTN;
import static mavmi.telegram_bot.water_stuff_bot.constants.Phrases.*;
import static mavmi.telegram_bot.water_stuff_bot.constants.Requests.*;

@Component
public class Service extends AbsService {
    private final UserAuthentication userAuthentication;
    private final WaterContainer waterContainer;

    public Service(
            Logger logger,
            UserAuthentication userAuthentication,
            WaterContainer waterContainer
    ) {
        super(logger);
        this.userAuthentication = userAuthentication;
        this.waterContainer = waterContainer;
    }

    @Override
    public void handleRequest(Message telegramMessage) {
        User telegramUser = telegramMessage.from();
        long chatId = telegramUser.id();
        String username = telegramUser.username();
        String firstName = telegramUser.firstName();
        String lastName = telegramUser.lastName();  
        String msg = telegramMessage.text();

        ServiceUser user = getUser(chatId, username, firstName, lastName);

        logEvent(user, msg);
        if (!userAuthentication.isPrivilegeGranted(chatId, BotNames.WATER_STUFF_BOT)) {
            logEvent(user, "Access denied");
            return;
        }
        if (msg == null) {
            logEvent(user, "Message is NULL");
            return;
        }

        IMenu userMenu = user.getMenu();
        if (msg.equals(CANCEL_REQ)) {
            cancelOperation(user);
        } else if (userMenu == Menu.MAIN_MENU) {
            switch (msg) {
                case (ADD_GROUP_REQ) -> add_askForName(user);
                case (RM_GROUP_REQ) -> rm_askForName(user);
                case (GET_INFO_REQ) -> getInfo(user);
                case (WATER_REQ) -> water_askForName(user, false);
                case (FERTILIZE_REQ) -> water_askForName(user, true);
                case (EDIT_GROUP_REQ) -> edit_askForName(user);
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
        telegramBot.sendMessage(user.getUserId(), ADD_GROUP_MSG);
    }

    private void add_approve(ServiceUser user, String msg) {
        user.setMenu(Menu.ADD_APPROVE);
        user.getLastMessages().add(msg);
        telegramBot.sendMessage(
                new SendMessage(user.getUserId(), APPROVE_MSG).
                        replyMarkup(((Bot) telegramBot).generateApproveKeyboard())
        );
    }

    private void add_process(ServiceUser user, String msg) {
        if (!msg.equals(YES_BTN)) {
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
            waterInfo.setName(name);
            waterInfo.setDiff(diff);
            waterInfo.setWater(WaterInfo.NULL_STR);
            waterInfo.setFertilize(WaterInfo.NULL_STR);
            waterContainer.put(waterInfo);

            telegramBot.sendMessage(user.getUserId(), SUCCESS_MSG);
        } catch (NumberFormatException | DataException e) {
            logger.err(e.getMessage());
            telegramBot.sendMessage(user.getUserId(), INVALID_GROUP_NAME_FORMAT_MSG);
        } finally {
            dropUserInfo(user);
        }
    }

    private void rm_askForName(ServiceUser user) {
        if (waterContainer.size() == 0) {
            telegramBot.sendMessage(user.getUserId(), ON_EMPTY_MSG);
        } else {
            user.setMenu(Menu.RM);
            telegramBot.sendMessage(
                    new SendMessage(user.getUserId(), ENTER_GROUP_NAME_MSG)
                            .replyMarkup(((Bot) telegramBot).generateGroupsKeyboard(getGroupsNames()))
            );
        }
    }

    private void rm_approve(ServiceUser user, String msg) {
        if (waterContainer.get(msg) == null) {
            telegramBot.sendMessage(user.getUserId(), INVALID_GROUP_NAME_MSG);
            dropUserInfo(user);
        } else {
            user.setMenu(Menu.RM_APPROVE);
            user.getLastMessages().add(msg);
            telegramBot.sendMessage(
                    new SendMessage(user.getUserId(), APPROVE_MSG).
                            replyMarkup(((Bot) telegramBot).generateApproveKeyboard())
            );
        }
    }

    private void rm_process(ServiceUser user, String msg) {
        if (!msg.equals(YES_BTN)) {
            cancelOperation(user);
        } else {
            waterContainer.remove(user.getLastMessages().get(0));
            telegramBot.sendMessage(user.getUserId(), SUCCESS_MSG);
            dropUserInfo(user);
        }
    }

    private void getInfo(ServiceUser user) {
        if (waterContainer.size() == 0) {
            telegramBot.sendMessage(user.getUserId(), ON_EMPTY_MSG);
        } else {
            StringBuilder builder = new StringBuilder();

            for (Map.Entry<String, WaterInfo> entry : waterContainer.entrySet()) {
                WaterInfo waterInfo = entry.getValue();

                builder.append("***")
                        .append("> ")
                        .append(waterInfo.getName())
                        .append("***")
                        .append("\n")
                        .append("Разница по дням: ")
                        .append(waterInfo.getDiff())
                        .append("\n")
                        .append("Полив: ")
                        .append(waterInfo.getWaterAsString())
                        .append("\n")
                        .append("Удобрение: ")
                        .append(waterInfo.getFertilizeAsString())
                        .append("\n\n");
            }

            telegramBot.sendMessage(user.getUserId(), builder.toString(), ParseMode.Markdown);
        }
    }

    private void water_askForName(ServiceUser user, boolean fertilize) {
        if (waterContainer.size() == 0){
            telegramBot.sendMessage(user.getUserId(), ON_EMPTY_MSG);
        } else {
            user.setMenu((fertilize) ? Menu.FERTILIZE : Menu.WATER);
            telegramBot.sendMessage(
                    new SendMessage(user.getUserId(), ENTER_GROUP_NAME_MSG)
                            .replyMarkup(((Bot) telegramBot).generateGroupsKeyboard(getGroupsNames())));
        }
    }

    private void water_process(ServiceUser user, String msg) {
        WaterInfo waterInfo = waterContainer.get(msg);
        if (waterInfo == null) {
            telegramBot.sendMessage(user.getUserId(), INVALID_GROUP_NAME_MSG);
        } else {
            boolean fertilize = user.getMenu() == Menu.FERTILIZE;
            Date date = Date.valueOf(LocalDate.now());
            waterInfo.setWater(date);
            if (fertilize) {
                waterInfo.setFertilize(date);
            }
            waterContainer.saveToFile();
            telegramBot.sendMessage(user.getUserId(), SUCCESS_MSG);
        }

        dropUserInfo(user);
    }

    private void edit_askForName(ServiceUser user) {
        if (waterContainer.size() == 0){
            telegramBot.sendMessage(user.getUserId(), ON_EMPTY_MSG);
        } else {
            user.setMenu(Menu.EDIT_GROUP_1);
            telegramBot.sendMessage(
                    new SendMessage(user.getUserId(), ENTER_GROUP_NAME_MSG)
                            .replyMarkup(((Bot) telegramBot).generateGroupsKeyboard(getGroupsNames())));
        }
    }

    private void edit_askForData(ServiceUser user, String msg) {
        if (waterContainer.get(msg) == null) {
            telegramBot.sendMessage(user.getUserId(), INVALID_GROUP_NAME_MSG);
            dropUserInfo(user);
        } else {
            user.setMenu(Menu.EDIT_GROUP_2);
            user.getLastMessages().add(msg);
            telegramBot.sendMessage(user.getUserId(), ENTER_GROUP_DATA_MSG);
        }
    }

    private void edit_process(ServiceUser user, String msg) {
        String[] splitted = msg.split("\n");

        try {
            if (splitted.length != 4) {
                throw new RuntimeException(INVALID_GROUP_NAME_FORMAT_MSG);
            }

            WaterInfo waterInfo = waterContainer.get(user.getLastMessages().get(0));
            waterInfo.setName(splitted[0]);
            waterInfo.setDiff(Integer.parseInt(splitted[1]));
            waterInfo.setWater(splitted[2]);
            waterInfo.setFertilize(splitted[3]);
            waterContainer.saveToFile();

            telegramBot.sendMessage(user.getUserId(), SUCCESS_MSG);
        } catch (RuntimeException e) {
            logger.err(e.getMessage());
            telegramBot.sendMessage(user.getUserId(), INVALID_GROUP_NAME_FORMAT_MSG);
        }

        dropUserInfo(user);
    }

    private void cancelOperation(ServiceUser user) {
        dropUserInfo(user);
        telegramBot.sendMessage(user.getUserId(), OPERATION_CANCELED_MSG);
    }

    private void error(ServiceUser user) {
        telegramBot.sendMessage(user.getUserId(), ERROR_MSG);
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

    private String[] getGroupsNames() {
        int size = waterContainer.size();
        String[] arr = new String[size];

        int i = 0;
        for (Map.Entry<String, WaterInfo> entry : waterContainer.entrySet()) {
            arr[i++] = entry.getKey();
        }

        return arr;
    }
}
