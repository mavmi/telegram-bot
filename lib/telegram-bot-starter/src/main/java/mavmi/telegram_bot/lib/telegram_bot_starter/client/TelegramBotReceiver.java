package mavmi.telegram_bot.lib.telegram_bot_starter.client;

import com.pengrad.telegrambot.TelegramBot;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TelegramBotReceiver {

    protected final TelegramBot telegramBot;

    public abstract void run();
}
