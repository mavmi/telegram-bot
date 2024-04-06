package mavmi.telegram_bot.common.telegramBot;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

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

    synchronized public void sendMessage(Long chatId, String msg) {
        sendMessage(new SendMessage(chatId, msg));
    }

    synchronized public void sendMessage(Long chatId, String msg, ParseMode parseMode) {
        sendMessage(new SendMessage(chatId, msg).parseMode(parseMode));
    }
}
