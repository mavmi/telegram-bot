package mavmi.telegram_bot.water_stuff.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import org.springframework.stereotype.Component;

@Component
public class WaterTelegramBotSender extends TelegramBotSender {

    public WaterTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

    public void sendText(long chatId, String msg) {
        sendTextMessage(chatId, msg, ParseMode.Markdown);
    }

    public void sendTextDeleteKeyboard(long chatId, String msg) {
        sendReplyKeyboard(chatId, msg, ParseMode.Markdown, new ReplyKeyboardRemove());
    }
}
