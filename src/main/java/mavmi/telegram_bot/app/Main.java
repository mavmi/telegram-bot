package mavmi.telegram_bot.app;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.SendChatAction;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.boot.autoconfigure.sendgrid.SendGridProperties;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        System.out.print("Enter bot token: ");
        TelegramBot telegramBot = new TelegramBot(new Scanner(System.in).nextLine());

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
