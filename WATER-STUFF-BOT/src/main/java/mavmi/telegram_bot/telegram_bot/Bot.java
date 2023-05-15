package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.water.Calen;
import mavmi.telegram_bot.water.WaterContainer;
import mavmi.telegram_bot.water.WaterInfo;

import java.util.GregorianCalendar;

import static mavmi.telegram_bot.constants.Levels.*;
import static mavmi.telegram_bot.constants.Phrases.*;
import static mavmi.telegram_bot.constants.Requests.*;

public class Bot {
    private class NotificationThread extends Thread{
        private final static long sleepDuration = 3 * 3600000L;
        private final Bot bot;
        private final long chatId;

        public NotificationThread(Bot bot, long chatId){
            this.bot = bot;
            this.chatId = chatId;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    String msg = generateMessage();
                    if (msg != null){
                        bot.sendMsg(new SendMessage(chatId, msg));
                    }
                    sleep(sleepDuration);
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                }
            }
        }

        private String generateMessage(){
            for (int i = 0; i < bot.waterContainer.size(); i++){

            }
            return null;
        }
    }

    private String availableUser;
    private WaterContainer waterContainer;
    private Logger logger;
    private TelegramBot telegramBot;

    private int userState;

    public Bot(){
        userState = MAIN_LEVEL;
    }

    public Bot setTelegramBot(String token){
        telegramBot = new TelegramBot(token);
        return this;
    }
    public Bot setLogger(Logger logger){
        this.logger = logger;
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

    public void run(){
        logger.log("WATER-STUFF-BOT IS RUNNING");
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                if (update.message() == null) continue;
                logger.log(generateLogLine(update));

                final long chatId = update.message().chat().id();
                final String inputText = update.message().text();
                final String username = update.message().from().username();

                if (!username.equals(availableUser)) continue;
                if (inputText == null) continue;

                if (userState == MAIN_LEVEL) {
                    switch (inputText){
                        case (START_REQ) -> greetings(chatId);
                        case (ADD_GROUP_REQ) -> addGroup(chatId, inputText);
                        case (RM_GROUP_REQ) -> rmGroup(chatId, inputText);
                        case (GET_INFO_REQ) -> getWaterInfo(chatId);
                        case (WATER_REQ) -> water(chatId, inputText, false);
                        case (FERTILIZE_REQ) -> water(chatId, inputText, true);
                        default -> error(chatId);
                    }
                } else {
                    if (inputText.equals(CANCEL_REQ)){
                        userState = MAIN_LEVEL;
                        sendMsg(new SendMessage(chatId, OPERATION_CANCELED_MSG));
                        continue;
                    }

                    if (userState == ADD_GROUP_LEVEL){
                        addGroup(chatId, inputText);
                    } else if (userState == RM_GROUP_LEVEL){
                        rmGroup(chatId, inputText);
                    } else if (userState == WATER_LEVEL){
                        water(chatId, inputText, false);
                    } else if (userState == FERTILIZE_LEVEL){
                        water(chatId, inputText, true);
                    }
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    private synchronized void sendMsg(SendMessage sendMessage){
        telegramBot.execute(sendMessage);
    }

    private void greetings(long chatId){
        sendMsg(new SendMessage(chatId, GREETINGS_MSG));
    }
    private void error(long chatId) {
        sendMsg(new SendMessage(chatId, ERROR_MSG));
    }
    private void getWaterInfo(long chatId){
        if (waterContainer.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
        } else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < waterContainer.size(); i++){
                WaterInfo waterInfo = waterContainer.get(i);
                Calen water = waterInfo.getWater();
                Calen fertilize = waterInfo.getFertilize();
                builder.append("```")
                        .append("\n")
                        .append(" << ")
                        .append(waterInfo.getName())
                        .append(" >>")
                        .append("\n")
                        .append("Полив: ")
                        .append((water != null) ? water.toHumanReadableString() : water)
                        .append("\n")
                        .append("Удобрение: ")
                        .append((fertilize != null) ? fertilize.toHumanReadableString() : fertilize)
                        .append("```");
                if (i + 1 != waterContainer.size()){
                    builder.append("\n").append("\n");
                }
            }
            sendMsg(new SendMessage(chatId, builder.toString()).parseMode(ParseMode.Markdown));
        }
    }
    private void addGroup(long chatId, String msg){
        if (userState == MAIN_LEVEL) {
            userState = ADD_GROUP_LEVEL;
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG));
        } else {
            waterContainer.add(new WaterInfo().setName(msg));
            waterContainer.toFile();
            sendMsg(new SendMessage(chatId, SUCCESS_MSG));
            userState = MAIN_LEVEL;
        }
    }
    private void rmGroup(long chatId, String msg){
        if (waterContainer.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
            return;
        }
        if (userState == MAIN_LEVEL) {
            userState = RM_GROUP_LEVEL;
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG).replyMarkup(generateGroupsKeyboard()));
        } else {
            for (int i = 0; i < waterContainer.size(); i++){
                WaterInfo waterInfo = waterContainer.get(i);
                if (waterInfo.getName().equals(msg)){
                    waterContainer.remove(i);
                    waterContainer.toFile();
                    sendMsg(new SendMessage(chatId, SUCCESS_MSG));
                    break;
                } else if (i + 1 == waterContainer.size()){
                    sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
                }
            }
            userState = MAIN_LEVEL;
        }
    }
    private void water(long chatId, String msg, boolean fertilize){
        if (waterContainer.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
            return;
        }
        if (userState == MAIN_LEVEL){
            userState = (fertilize) ? FERTILIZE_LEVEL : WATER_LEVEL;
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG).replyMarkup(generateGroupsKeyboard()));
        } else {
            WaterInfo waterInfo = getWaterInfoByName(msg);
            if (waterInfo == null){
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
                return;
            }
            waterInfo.setWater(new Calen(GregorianCalendar.getInstance()));
            if (fertilize){
                waterInfo.setFertilize(new Calen(GregorianCalendar.getInstance()));
            }
            waterContainer.toFile();
            sendMsg(new SendMessage(chatId, SUCCESS_MSG));
            userState = MAIN_LEVEL;
        }
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
    private ReplyKeyboardMarkup generateGroupsKeyboard(){
        KeyboardButton[] buttons = new KeyboardButton[waterContainer.size()];
        for (int i = 0; i < waterContainer.size(); i++){
            buttons[i] = new KeyboardButton(waterContainer.get(i).getName());
        }
        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
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

}
