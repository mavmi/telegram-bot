package mavmi.telegram_bot.monitoring.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

/**
 * {@inheritDoc}
 */
@Component
public class MonitoringTelegramBotSender extends TelegramBotSender {

    public MonitoringTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

    public void sendText(long chatId, String msg) {
        sendText(List.of(chatId), msg);
    }

    public void sendText(List<Long> chatIdx, String msg) {
        for (Long chatId : chatIdx) {
            sendMessage(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown).replyMarkup(new ReplyKeyboardRemove()));
        }
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sendReplyKeyboard(List.of(chatId), msg, keyboardButtons);
    }

    public void sendReplyKeyboard(List<Long> chatIdx, String msg, String[] keyboardButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : keyboardButtons) {
            replyKeyboardMarkup.addRow(button);
        }

        for (Long chatId : chatIdx) {
            sendMessage(new SendMessage(chatId, msg)
                    .replyMarkup(replyKeyboardMarkup)
                    .parseMode(ParseMode.Markdown));
        }
    }

    public void sendFile(List<Long> chatIdx, File file){
        for (Long id : chatIdx){
            sendRequest(new SendDocument(id, file));
        }
    }
}
