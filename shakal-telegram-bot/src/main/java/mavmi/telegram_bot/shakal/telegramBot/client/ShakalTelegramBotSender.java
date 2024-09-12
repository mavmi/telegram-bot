package mavmi.telegram_bot.shakal.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.SneakyThrows;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.telegramBot.client.TelegramBotSender;
import mavmi.telegram_bot.shakal.cache.ShakalDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShakalTelegramBotSender extends TelegramBotSender {

    @Autowired
    private CacheComponent cacheComponent;

    public ShakalTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

    public void sendText(long chatId, String msg) {
        sendTextMessage(
                chatId,
                msg,
                ParseMode.Markdown
        );
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sendTextMessage(
                chatId,
                msg,
                ParseMode.Markdown,
                new ReplyKeyboardRemove()
        );
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : keyboardButtons) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));
    }

    @SneakyThrows
    public void sendDice(long chatId, String msg, String[] keyboardButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : keyboardButtons) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));

        int botDiceValue = sendRequest(new SendDice(chatId)).message().dice().value();
        cacheComponent.getCacheBucket().getDataCache(ShakalDataCache.class).setBotDice(botDiceValue);
    }
}
