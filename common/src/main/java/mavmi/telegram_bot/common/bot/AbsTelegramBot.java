package mavmi.telegram_bot.common.bot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.request.BaseRequest;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.BaseResponse;
import lombok.Getter;
import mavmi.telegram_bot.common.logger.Logger;

@Getter
public abstract class AbsTelegramBot {
    protected final Logger logger;
    protected final TelegramBot telegramBot;

    public AbsTelegramBot(Logger logger, String botToken){
        this.logger = logger;
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

    protected void logEvent(Message message){
        com.pengrad.telegrambot.model.User user = message.from();
        logger.log(
                "USER_ID: [" +
                        user.id() +
                        "], " +
                        "USERNAME: [" +
                        user.username() +
                        "], " +
                        "FIRSTNAME: [" +
                        user.firstName() +
                        "], " +
                        "LASTNAME: [" +
                        user.lastName() +
                        "], " +
                        "MESSAGE: [" +
                        message.text() +
                        "]"
        );
    }
}
