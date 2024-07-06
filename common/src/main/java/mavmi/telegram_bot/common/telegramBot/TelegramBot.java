package mavmi.telegram_bot.common.telegramBot;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

import java.io.File;

/**
 * Base telegram bots class.
 * Contains common logic
 */
public abstract class TelegramBot {

    protected final com.pengrad.telegrambot.TelegramBot telegramBot;

    public TelegramBot(String botToken){
        this.telegramBot = new com.pengrad.telegrambot.TelegramBot(botToken);
    }

    public abstract void run();

    synchronized public <T extends BaseRequest<T, R>, R extends BaseResponse> R sendRequest(BaseRequest<T, R> baseRequest) {
        return telegramBot.execute(baseRequest);
    }

    synchronized public void sendMessage(SendMessage sendMessage) {
        telegramBot.execute(sendMessage);
    }

    synchronized public void sendTextMessage(long chatId, String msg) {
        sendMessage(new SendMessage(chatId, msg));
    }

    synchronized public void sendTextMessage(long chatId, String msg, ParseMode parseMode) {
        sendMessage(new SendMessage(chatId, msg).parseMode(parseMode));
    }

    synchronized public void sendFile(long chatIg, File file) {
        sendRequest(new SendDocument(chatIg, file));
    }
}
