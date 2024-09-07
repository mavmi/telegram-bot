package mavmi.telegram_bot.common.telegramBot.userThread;

import com.pengrad.telegrambot.model.Update;

public interface UserThread extends Runnable {

    void add(Update update);
}
