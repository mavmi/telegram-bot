package mavmi.telegram_bot.common.telegramBot;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.*;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;

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

    synchronized public SendResponse sendMessage(SendMessage sendMessage) {
        return sendRequest(sendMessage);
    }

    synchronized public SendResponse sendTextMessage(long chatId, String msg) {
        return sendMessage(new SendMessage(chatId, msg));
    }

    synchronized public SendResponse sendTextMessage(long chatId, String msg, ParseMode parseMode) {
        return sendMessage(new SendMessage(chatId, msg).parseMode(parseMode));
    }

    synchronized public SendResponse sendTextMessage(long chatId, String msg, ParseMode parseMode, Keyboard keyboard) {
        return sendMessage(new SendMessage(chatId, msg).parseMode(parseMode).replyMarkup(keyboard));
    }

    synchronized public SendResponse sendTextMessage(long chatId, String msg, Keyboard keyboard) {
        return sendMessage(new SendMessage(chatId, msg).replyMarkup(keyboard));
    }

    synchronized public SendResponse sendFile(long chatId, File file) {
        return sendRequest(new SendDocument(chatId, file));
    }

    synchronized public SendResponse sendImage(long chatId, File file) {
        return sendRequest(new SendPhoto(chatId, file));
    }

    synchronized public SendResponse sendImage(long chatId, File file, String textMessage) {
        return sendRequest(new SendPhoto(chatId, file).caption(textMessage));
    }

    synchronized public BaseResponse deleteMessage(long chatId, int msgId) {
        return sendRequest(new DeleteMessage(chatId, msgId));
    }
}
