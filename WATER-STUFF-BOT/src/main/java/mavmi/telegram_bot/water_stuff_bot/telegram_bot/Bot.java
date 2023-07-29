package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.common.auth.BotNames;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.database.model.RuleModel;
import mavmi.telegram_bot.common.database.model.WaterStuffModel;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.database.repository.WaterStuffRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.constants.Requests;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static mavmi.telegram_bot.water_stuff_bot.constants.Buttons.NO_BTN;
import static mavmi.telegram_bot.water_stuff_bot.constants.Buttons.YES_BTN;
import static mavmi.telegram_bot.water_stuff_bot.constants.Levels.*;
import static mavmi.telegram_bot.water_stuff_bot.constants.Phrases.*;

public class Bot extends AbsTelegramBot {
    private final UserAuthentication userAuthentication;
    private final RuleRepository ruleRepository;
    private final WaterStuffRepository waterStuffRepository;
    private final TelegramBot telegramBot;

    private final List<NotificationThread> notificationThreads;
    private final List<Integer> userStates;
    private final List<String> msgs;

    public Bot(String telegramBotToken, Logger logger, UserAuthentication userAuthentication, RuleRepository ruleRepository, WaterStuffRepository waterStuffRepository){
        super(logger);
        this.telegramBot = new TelegramBot(telegramBotToken);
        this.userAuthentication = userAuthentication;
        this.ruleRepository = ruleRepository;
        this.waterStuffRepository = waterStuffRepository;

        userStates = new ArrayList<>();
        msgs = new ArrayList<>();
        notificationThreads = new ArrayList<>();
        userStates.add(MAIN_LEVEL);
    }

    @Override
    public void run(){
        logger.log("WATER-STUFF-BOT IS RUNNING");
        startNotificationThreads();
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates){
                if (update.message() == null) continue;
                Message message = update.message();
                long chatId = message.chat().id();
                String inputText = message.text();

                if (!userAuthentication.isPrivilegeGranted(update.message().from().id(), BotNames.WATER_STUFF_BOT)) continue;
                if (inputText == null) continue;
                logEvent(message);
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

    private void greetings(long chatId){
        sendMsg(new SendMessage(chatId, GREETINGS_MSG));
        msgs.remove(msgs.size() - 1);
    }
    private void error(long chatId) {
        sendMsg(new SendMessage(chatId, ERROR_MSG));
        msgs.remove(msgs.size() - 1);
    }
    private void getWaterInfo(long chatId){
        List<WaterStuffModel> waterStuffModelList = waterStuffRepository.getAll();
        if (waterStuffModelList.size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
        } else {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < waterStuffModelList.size(); i++){
                WaterStuffModel waterStuffModel = waterStuffModelList.get(i);
                builder.append("***")
                        .append("> ")
                        .append(waterStuffModel.getName())
                        .append("***")
                        .append("\n")
                        .append("Разница по дням: ")
                        .append(waterStuffModel.getDiff())
                        .append("\n")
                        .append("Полив: ")
                        .append(((waterStuffModel.getWater() != null) ? dateTimeFormatter.format(waterStuffModel.getWater().toLocalDate()) : "null"))
                        .append("\n")
                        .append("Удобрение: ")
                        .append(((waterStuffModel.getFertilize() != null) ? dateTimeFormatter.format(waterStuffModel.getFertilize().toLocalDate()) : "null"));

                if (i + 1 != waterStuffModelList.size()){
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

                waterStuffRepository.insert(new WaterStuffModel(
                        0L,
                        name,
                        null,
                        null,
                        diff
                ));
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
            if (waterStuffRepository.get(msgs.get(msgs.size() - 1)) == null){
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
                drop();
            } else {
                userStates.add(EDIT_GROUP_LEVEL_2);
                sendMsg(new SendMessage(chatId, ENTER_GROUP_DATA_MSG));
            }
        } else if (userStates.get(userStates.size() - 1) == EDIT_GROUP_LEVEL_2){
            WaterStuffModel waterStuffModel = waterStuffRepository.get(msgs.get(msgs.size() - 2));
            String[] splitted = msgs.get(msgs.size() - 1).split("\n");
            try {
                if (splitted.length != 4) throw new RuntimeException(INVALID_GROUP_NAME_FORMAT_MSG);
                waterStuffModel.setName(splitted[0]);
                waterStuffModel.setDiff(Integer.parseInt(splitted[1]));
                waterStuffModel.setWater((splitted[2].equals("null")) ? null : Date.valueOf(splitted[2]));
                waterStuffModel.setFertilize((splitted[3].equals("null")) ? null : Date.valueOf(splitted[3]));
                waterStuffRepository.update(waterStuffModel);
            } catch (RuntimeException e) {
                logger.err(e.getMessage());
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_FORMAT_MSG));
            }
            drop();
        }
    }
    private void rmGroup(long chatId){
        if (waterStuffRepository.getAll().size() == 0){
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
                WaterStuffModel waterStuffModel = waterStuffRepository.get(msgs.get(msgs.size() - 2));
                if (waterStuffModel != null){
                    waterStuffRepository.remove(waterStuffModel);
                    sendMsg(new SendMessage(chatId, SUCCESS_MSG));
                } else {
                    sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
                }
                drop();
            }
        }
    }
    private void water(long chatId, boolean fertilize){
        if (waterStuffRepository.getAll().size() == 0){
            sendMsg(new SendMessage(chatId, ON_EMPTY_MSG));
            msgs.remove(msgs.size() - 1);
            return;
        }
        if (userStates.get(userStates.size() - 1) == MAIN_LEVEL){
            userStates.add((fertilize) ? FERTILIZE_LEVEL : WATER_LEVEL);
            sendMsg(new SendMessage(chatId, ENTER_GROUP_NAME_MSG).replyMarkup(generateGroupsKeyboard()));
        } else {
            WaterStuffModel waterStuffModel = waterStuffRepository.get(msgs.get(msgs.size() - 1));
            if (waterStuffModel == null){
                sendMsg(new SendMessage(chatId, INVALID_GROUP_NAME_MSG));
            } else {
                try {
                    waterStuffModel.setWater(Date.valueOf(LocalDate.now()));
                    if (fertilize){
                        waterStuffModel.setFertilize(Date.valueOf(LocalDate.now()));
                    }
                    waterStuffRepository.update(waterStuffModel);
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

    private void startNotificationThreads(){
        for (RuleModel ruleModel: ruleRepository.getAll()){
            Boolean value = ruleModel.getWaterStuff();
            if (value != null && value){
                NotificationThread notificationThread = new NotificationThread(this, waterStuffRepository, logger, ruleModel.getUserid());
                notificationThread.start();
                notificationThreads.add(notificationThread);
            }
        }
    }
    private ReplyKeyboardMarkup generateGroupsKeyboard(){
        List<WaterStuffModel> waterStuffModelList = waterStuffRepository.getAll();
        KeyboardButton[] buttons = new KeyboardButton[waterStuffModelList.size()];
        for (int i = 0; i < waterStuffModelList.size(); i++){
            buttons[i] = new KeyboardButton(waterStuffModelList.get(i).getName());
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
}
