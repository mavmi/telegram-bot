package mavmi.telegram_bot.common.bot;

import com.pengrad.telegrambot.model.Message;

public abstract class AbsTelegramBot {
    public abstract void run();
    protected abstract void logEvent(Message message);
    protected abstract boolean checkValidity();
}
