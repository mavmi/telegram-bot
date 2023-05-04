package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendInvoice;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

// 6254190556:AAEPFPdLO6BsGN8_yqD4RLN29In8qnHdbmI
public class Bot {
    private enum STATE{
        DEFAULT,
        APOLOCHEESE,
        WATER,
        FERTILIZE
    }

    private static final String GOOSE_REQ = "/goose";
    private static final String APOLOCHEESE_REQ = "/apolocheese";
    private static final String GET_INFO_REQ = "/info";
    private static final String WATER_REQ = "/water";
    private static final String FERTILIZE_REQ = "/fertilize";
    private static final Set<String> availableUsers = new HashSet<>();
    static {
        availableUsers.add("pmrjo");
    }

    private final TelegramBot telegramBot;

    private STATE state;

    public Bot(String token){
        state = STATE.DEFAULT;
        telegramBot = new TelegramBot(token);
    }

    public void run(){
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update update : updates){
                    if (!checkUsername(update.message().from().username())) continue;

                    final long chatId = update.message().chat().id();
                    final String inputText = update.message().text();

                    if (state == STATE.DEFAULT){
                        if (inputText.equals(APOLOCHEESE_REQ)) {
                            telegramBot.execute(new SendMessage(chatId, "Для кого оформляем?"));
                            state = STATE.APOLOCHEESE;
                        } else if (inputText.equals(GOOSE_REQ)) {
                            goose(chatId);
                        } else {
                            sendErrorMsg(chatId);
                        }
                    } else if (state == STATE.APOLOCHEESE){
                        apolocheese(chatId, inputText);
                        state = STATE.DEFAULT;
                    }

                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }

    private void goose(long chatId){
        final StringBuilder builder = new StringBuilder();
        builder.append("```\n")
                .append("░░░░░░░░░░░░░░░░░░░░\n")
                .append("░░░░░ЗАПУСКАЕМ░░░░░░░\n")
                .append("░ГУСЯ░▄▀▀▀▄░РАБОТЯГИ░░\n")
                .append("▄███▀░◐░░░▌░░░░░░░░░\n")
                .append("░░░░▌░░░░░▐░░░░░░░░░\n")
                .append("░░░░▐░░░░░▐░░░░░░░░░\n")
                .append("░░░░▌░░░░░▐▄▄░░░░░░░\n")
                .append("░░░░▌░░░░▄▀▒▒▀▀▀▀▄\n")
                .append("░░░▐░░░░▐▒▒▒▒▒▒▒▒▀▀▄\n")
                .append("░░░▐░░░░▐▄▒▒▒▒▒▒▒▒▒▒▀▄\n")
                .append("░░░░▀▄░░░░▀▄▒▒▒▒▒▒▒▒▒▒▀▄\n")
                .append("░░░░░░▀▄▄▄▄▄█▄▄▄▄▄▄▄▄▄▄▄▀▄\n")
                .append("░░░░░░░░░░░▌▌░▌▌░░░░░\n")
                .append("░░░░░░░░░░░▌▌░▌▌░░░░░\n")
                .append("░░░░░░░░░▄▄▌▌▄▌▌░░░░░\n")
                .append("```");

        telegramBot.execute(new SendMessage(chatId, builder.toString()).parseMode(ParseMode.Markdown));
    }
    private void apolocheese(long chatId, String username){
        final StringBuilder builder = new StringBuilder();
        builder.append("```\n")
                .append("java -jar \"/home/mavmi/apolocheese/apolocheese.jar\"")
                .append("\n\n")
                .append(new SimpleDateFormat("dd.MM.yyyy HH:mm:").format(GregorianCalendar.getInstance().getTime()))
                .append("```")
                .append("\n")
                .append("\"Я прошу прощения, ")
                .append(username)
                .append("! Солнышко! Я дико извиняюсь! Сможешь ли ты меня простить?.....\"")
                .append("\n")
                .append("\n")
                .append("```\n")
                .append("@https://github.com/mavmi\n")
                .append("@All rights reserved!\n")
                .append("@Do not distribute!\n")
                .append("```");

        telegramBot.execute(new SendMessage(chatId, builder.toString()).parseMode(ParseMode.Markdown));
    }

    private void sendErrorMsg(long chatId){
        telegramBot.execute(new SendMessage(chatId, "я не выкупаю..."));
    }

    private boolean checkUsername(String username){
        return availableUsers.contains(username);
    }
}
