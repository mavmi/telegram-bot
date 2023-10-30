package mavmi.telegram_bot.shakal_bot.telegram_bot;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import mavmi.telegram_bot.shakal_bot.constants.Levels;
import mavmi.telegram_bot.shakal_bot.constants.Requests;

public class ProcessRequest extends Thread {
    private final Bot bot;
    private final Update update;

    public ProcessRequest(Bot bot, Update update) {
        this.bot = bot;
        this.update = update;
    }

    @Override
    public void run() {
        final long chatId = update.message().chat().id();
        final String inputText = update.message().text();
        final User user = bot.processUsername(update.message());

        if (user.getState() == Levels.MAIN_LEVEL) {
            if (inputText == null) return;
            switch (inputText) {
                case (Requests.START_REQ) -> bot.greetings(chatId);
                case (Requests.APOLOCHEESE_REQ) -> bot.apolocheese(chatId, inputText, user);
                case (Requests.GOOSE_REQ) -> bot.goose(chatId);
                case (Requests.ANEK_REQ) -> bot.anek(chatId);
                case (Requests.MEME_REQ) -> bot.meme(chatId);
                case (Requests.DICE_REQ) -> bot.dice(chatId, user, update.message());
                case (Requests.HOROSCOPE_REQ) -> bot.horoscope(chatId, user, update.message());
                default -> bot.sendMessage(chatId, bot.generateErrorMsg());
            }
        } else if (user.getState() == Levels.APOLOCHEESE_LEVEL) {
            bot.apolocheese(chatId, inputText, user);
        } else if (user.getState() == Levels.DICE_LEVEL) {
            bot.dice(chatId, user, update.message());
        } else if (user.getState() == Levels.HOROSCOPE_LEVEL) {
            bot.horoscope(chatId, user, update.message());
        }
    }
}
