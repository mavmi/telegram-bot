package mavmi.telegram_bot.shakal.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.SneakyThrows;
import mavmi.telegram_bot.common.cache.impl.CacheComponent;
import mavmi.telegram_bot.common.telegramBot.TelegramBotSender;
import mavmi.telegram_bot.shakal.cache.ShakalServiceDataCache;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShakalTelegramBotSender extends TelegramBotSender {

    @Autowired
    private CacheComponent cacheComponent;

    public ShakalTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

    public void sendText(long chatId, ShakalServiceRs shakalServiceRs) {
        sendTextMessage(
                chatId,
                shakalServiceRs.getMessageJson().getTextMessage(),
                ParseMode.Markdown
        );
    }

    public void sendReplyKeyboard(long chatId, ShakalServiceRs shakalServiceRs) {
        String msg = shakalServiceRs.getMessageJson().getTextMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : shakalServiceRs.getReplyKeyboardJson().getKeyboardButtons()) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));
    }

    @SneakyThrows
    public void sendDice(long chatId, ShakalServiceRs shakalServiceRs) {
        String msg = shakalServiceRs.getMessageJson().getTextMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : shakalServiceRs.getReplyKeyboardJson().getKeyboardButtons()) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));

        int botDiceValue = sendRequest(new SendDice(chatId)).message().dice().value();
        cacheComponent.getCacheBucket().getDataCache(ShakalServiceDataCache.class).setBotDice(botDiceValue);
    }
}
