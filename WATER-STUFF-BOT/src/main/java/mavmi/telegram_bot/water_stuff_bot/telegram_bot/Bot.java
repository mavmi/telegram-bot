package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.utils.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.water.Calen;
import mavmi.telegram_bot.water_stuff_bot.water.WaterContainer;
import mavmi.telegram_bot.water_stuff_bot.water.WaterInfo;
import mavmi.telegram_bot.water_stuff_bot.constants.Requests;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static mavmi.telegram_bot.water_stuff_bot.constants.Buttons.*;
import static mavmi.telegram_bot.water_stuff_bot.constants.Levels.*;
import static mavmi.telegram_bot.water_stuff_bot.constants.Phrases.*;

public class Bot {
    private String availableUser;
    private Long availableChatId;
    private WaterContainer waterContainer;
    private Logger logger;
    private TelegramBot telegramBot;

    private NotificationThread notificationThread;
    private List<Integer> userStates;
    private List<String> msgs;

    public Bot(){
        userStates = new ArrayList<>();
        msgs = new ArrayList<>();
        notificationThread = null;
        logger = Logger.getInstance();

        userStates.add(MAIN_LEVEL);
    }

    public Bot setTelegramBot(String token){
        telegramBot = new TelegramBot(token);
        return this;
    }
    public Bot setLogger(){
        this.logger = Logger.getInstance();
        return this;
    }
    public Bot setWaterContainer(String workingFile){
        waterContainer = new WaterContainer(workingFile);
        return this;
    }
    public Bot setAvailableUser(String availableUser){
        this.availableUser = availableUser;
        return this;
    }
    public Bot setAvailableChatId(long chatId){
        this.availableChatId = chatId;
        return this;
    }

    public void run(){
        if (!checkValidity()) throw new RuntimeException("Bot is not set up");

        logger.log("WATER-STUFF-BOT IS RUNNING");
        notificationThread = new NotificationThread(this, availableChatId);
        notificationThread.start();

        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                if (update.message() == null) continue;
                logger.log(generateLogLine(update));

                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final String username = update.message().from().username();

                if (!username.equals(availableUser) || chatId != availableChatId) continue;
                if (inputText == null) continue;

                int state = userStates.get(userStates.size() - 1);
                msgs.add(inputText);

                if (state == MAIN_LEVEL) {
                    switch (inputText){
                        case (Requests.START_REQ) -> greetings(chatId);
                        case (Requests.ADD_GROUP_REQ) -> addGroup(chatId);
                        case (Requests.RM_GROUP_REQ) -> rmGroup(chatId);
                        case (Requests.GET_INFO_REQ) -> getWaterInfo(chatId);
                        case (Requests.WATER_REQ) -> water(chatId, false);
                        case (Requests.FERTILIZE_REQ) -> water(chatId, true);
                        case (Requests.EDIT_GROUP_REQ) -> editGroup(chatId);
                        default -> error(chatId);
                    }
                } else if (state == APPROVE_LEVEL){
                    int prevState = userStates.get(userStates.size() - 2);

                    if (prevState == RM_GROUP_LEVEL){
                        rmGroup(chatId);
                    } else if (prevState == ADD_GROUP_LEVEL){
                        addGroup(chatId);
                    }
                } else {
                    if (inputText.equals(Requests.CANCEL_REQ)){
                        cancel(chatId);
                        continue;
                    }

                    if (state == ADD_GROUP_LEVEL){
                        addGroup(chatId);
                    } else if (state == RM_GROUP_LEVEL){
                        rmGroup(chatId);
                    } else if (state == WATER_LEVEL){
                        water(chatId, false);
                    } else if (state == FERTILIZE_LEVEL){
                        water(chatId, true);
                    } else if (state == EDIT_GROUP_LEVEL_1 || state == EDIT_GROUP_LEVEL_2){
                        editGroup(chatId);
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    synchronized void sendMsg(SendMessage sendMessage){
        telegramBot.execute(sendMessage);
    }
    WaterContainer getWaterContainer(){
        return waterContainer;
    }

    private void greetings(long chatId){
        sendMsg(new SendMessage(chatId, GREETINGS_MSG));
        msgs.remove(msgs.size() - 1);
    }
    private void error(long chatId) {
        sendMsg(new SendMessage(chatId, ERROR_MSG));
        msgs.remove(msgs.size() - 1);
    }
    private void getWaterInfo(long chatId){
        if (waterContainer.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < waterContainer.size(); i++){
                builder.append(waterContainer.get(i).toInfoString());
                if (i + 1 != waterContainer.size()){
                    builder.append("\n").append("\n");
                }
            }
            sendMsg(new SendMessage(chatId, builder.toString()).parseMode(ParseMode.Markdown));
        }
        msgs.remove(msgs.size() - 1);
    }
    private void addGroup(long chatId){
        if (userStates.get(userStates.size() - 1) == MAIN_LEVEL) {
            userStates.add(ADD_GROUP_LEVEL);
            sendMsg(new SendMessage(chatId, ADD_GROUP_MSG));
            msgs.remove(msgs.size() - 1);
        } else if (userStates.get(userStates.size() - 1) == ADD_GROUP_LEVEL) {
            userStates.add(APPROVE_LEVEL);
            sendMsg(new SendMessage(chatId, APPROVE_MSG).replyMarkup(generateApproveKeyboard()));
        } else if (userStates.get(userStates.size() - 1) == APPROVE_LEVEL) {
            if (!msgs.get(msgs.size() - 1).equals(YES_BTN)) {
                cancel(chatId);
            } else {
                String name;
                int diff;

                try {
                    String[] splitted = msgs.get(msgs.size() - 2).replaceAll(" ", "").split(";");
                    if (splitted.length != 2) throw new NumberFormatException();
                    name = splitted[0];
                    diff = Integer.parseInt(splitted[1]);
                } catch (NumberFormatException e){
                    logger.err(e.getMessage());
                    sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_FORMAT_MSG));
                    cancel(chatId);
                    return;
                }

                waterContainer.add(new WaterInfo().setName(name).setDiff(diff));
                waterContainer.toFile();
                sendMsg(new SendMessage(chatId, SUCCESS_MSG));
                drop();
            }
        }
    }
    private void editGroup(long chatId){
        if (userStates.get(userStates.size() - 1) == MAIN_LEVEL){
            userStates.add(EDIT_GROUP_LEVEL_1);
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG).replyMarkup(generateGroupsKeyboard()));
            msgs.remove(msgs.size() - 1);
        } else if (userStates.get(userStates.size() - 1) == EDIT_GROUP_LEVEL_1){
            if (getWaterInfoByName(msgs.get(msgs.size() - 1)) == null){
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
                drop();
            } else {
                userStates.add(EDIT_GROUP_LEVEL_2);
                sendMsg(new SendMessage(chatId, ENTER_GROUP_DATA_MSG));
            }
        } else if (userStates.get(userStates.size() - 1) == EDIT_GROUP_LEVEL_2){
            WaterInfo waterInfo = getWaterInfoByName(msgs.get(msgs.size() - 2));
            String[] splitted = msgs.get(msgs.size() - 1).split("\n");
            try {
                if (splitted.length != 4) throw new RuntimeException(INVALID_GROUP_NAME_FORMAT_MSG);
                waterInfo
                        .setName(splitted[0])
                        .setDiff(Integer.parseInt(splitted[1]))
                        .setWater(new Calen(splitted[2]))
                        .setFertilize(new Calen(splitted[3]));
                waterContainer.toFile();
            } catch (RuntimeException e) {
                logger.err(e.getMessage());
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_FORMAT_MSG));
            }
            drop();
        }
    }
    private void rmGroup(long chatId){
        if (waterContainer.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
            msgs.remove(msgs.size() - 1);
            return;
        }

        if (userStates.get(userStates.size() - 1) == MAIN_LEVEL) {
            userStates.add(RM_GROUP_LEVEL);
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG).replyMarkup(generateGroupsKeyboard()));
            msgs.remove(msgs.size() - 1);
        } else if (userStates.get(userStates.size() - 1) == RM_GROUP_LEVEL){
            userStates.add(APPROVE_LEVEL);
            sendMsg(new SendMessage(chatId, APPROVE_MSG).replyMarkup(generateApproveKeyboard()));
        } else if (userStates.get(userStates.size() - 1) == APPROVE_LEVEL) {
            if (!msgs.get(msgs.size() - 1).equals(YES_BTN)) {
                cancel(chatId);
            } else {
                for (int i = 0; i < waterContainer.size(); i++){
                    WaterInfo waterInfo = waterContainer.get(i);
                    if (waterInfo.getName().equals(msgs.get(msgs.size() - 2))){
                        waterContainer.remove(i);
                        waterContainer.toFile();
                        sendMsg(new SendMessage(chatId, SUCCESS_MSG));
                        break;
                    } else if (i + 1 == waterContainer.size()){
                        sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
                    }
                }
                drop();
            }
        }
    }
    private void water(long chatId, boolean fertilize){
        if (waterContainer.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
            msgs.remove(msgs.size() - 1);
            return;
        }
        if (userStates.get(userStates.size() - 1) == MAIN_LEVEL){
            userStates.add((fertilize) ? FERTILIZE_LEVEL : WATER_LEVEL);
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG).replyMarkup(generateGroupsKeyboard()));
        } else {
            WaterInfo waterInfo = getWaterInfoByName(msgs.get(msgs.size() - 1));
            if (waterInfo == null){
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
            } else {
                try {
                    waterInfo.setWater(new Calen(GregorianCalendar.getInstance()));
                    if (fertilize){
                        waterInfo.setFertilize(new Calen(GregorianCalendar.getInstance()));
                    }
                    waterContainer.toFile();
                    sendMsg(new SendMessage(chatId, SUCCESS_MSG));
                } catch (Exception e){
                    logger.err(e.getMessage());
                }
            }
            userStates.remove(userStates.size() - 1);
        }
        msgs.remove(msgs.size() - 1);
    }
    private void cancel(long chatId){
        drop();
        sendMsg(new SendMessage(chatId, OPERATION_CANCELED_MSG));
    }
    private void drop(){
        userStates.clear();
        userStates.add(MAIN_LEVEL);
        msgs.clear();
    }

    private String generateLogLine(Update update){
        return new StringBuilder()
                .append("USER_ID: [")
                .append(update.message().from().id())
                .append("], ")
                .append("USERNAME: [")
                .append(update.message().from().username())
                .append("], ")
                .append("FIRST NAME: [")
                .append(update.message().from().firstName())
                .append("], ")
                .append("LAST NAME: [")
                .append(update.message().from().lastName())
                .append("], ")
                .append("MESSAGE: [")
                .append(update.message().text())
                .append("]")
                .toString();
    }
    private WaterInfo getWaterInfoByName(String name){
        for (int i = 0; i < waterContainer.size(); i++){
            WaterInfo waterInfo = waterContainer.get(i);
            if (waterInfo.getName().equals(name)){
                return waterInfo;
            }
        }
        return null;
    }
    private ReplyKeyboardMarkup generateGroupsKeyboard(){
        KeyboardButton[] buttons = new KeyboardButton[waterContainer.size()];
        for (int i = 0; i < waterContainer.size(); i++){
            buttons[i] = new KeyboardButton(waterContainer.get(i).getName());
        }
        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
    }
    private ReplyKeyboardMarkup generateApproveKeyboard(){
        KeyboardButton[] buttons = new KeyboardButton[]{
                new KeyboardButton(YES_BTN),
                new KeyboardButton(NO_BTN)
        };
        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    private boolean checkValidity(){
        return availableUser != null &&
                availableChatId != null &&
                waterContainer != null &&
                logger != null &&
                telegramBot != null;
    }

}
