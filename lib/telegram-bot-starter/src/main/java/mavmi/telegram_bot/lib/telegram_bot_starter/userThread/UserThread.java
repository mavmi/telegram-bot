package mavmi.telegram_bot.lib.telegram_bot_starter.userThread;

import com.pengrad.telegrambot.model.Update;

public interface UserThread extends Runnable {

    void add(Update update);
}
