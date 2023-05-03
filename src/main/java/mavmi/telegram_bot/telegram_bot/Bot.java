package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;

import java.util.List;

public class Bot {
    private final TelegramBot telegramBot;

    public Bot(String token){
        telegramBot = new TelegramBot(token);
    }

    public void run(){
        Keyboard inlineKeyboardMarkup = new ReplyKeyboardMarkup(
                new KeyboardButton("qwe")
        );

        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> updates) {
                for (Update update : updates){
                    System.out.println(update.message().text());
                    SendMessage sendMessage = new SendMessage(update.message().chat().id(), "<default answer>");
                    SendResponse sendResponse = telegramBot.execute(sendMessage);
                    System.out.println(sendResponse.isOk());
                    telegramBot.execute(new SendMessage(update.message().chat().id(), "<keyboard>").replyMarkup(inlineKeyboardMarkup));
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        });
    }
}
