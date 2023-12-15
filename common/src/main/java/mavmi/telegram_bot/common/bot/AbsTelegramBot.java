package mavmi.telegram_bot.common.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;

public abstract class AbsTelegramBot {
    protected final TelegramBot telegramBot;

    public AbsTelegramBot(String botToken){
        this.telegramBot = new TelegramBot(botToken);
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
