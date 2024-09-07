package mavmi.telegram_bot.shakal.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.TelegramBotReceiver;
import mavmi.telegram_bot.shakal.telegramBot.userThread.ShakalUserThreads;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ShakalTelegramBotReceiver extends TelegramBotReceiver {

    private final ShakalUserThreads userThreads;

    public ShakalTelegramBotReceiver(
            TelegramBot telegramBot,
            ShakalUserThreads userThreads
    ) {
        super(telegramBot);
        this.userThreads = userThreads;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                userThreads.add(update);
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            log.error(e.getMessage(), e);
        });
    }
}
