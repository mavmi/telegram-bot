package mavmi.telegram_bot.rocketchat.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotReceiver;
import mavmi.telegram_bot.rocketchat.telegramBot.userThread.RocketUserThreads;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RocketTelegramBotReceiver extends TelegramBotReceiver {

    private final RocketUserThreads userThreads;

    public RocketTelegramBotReceiver(TelegramBot telegramBot,
                                     RocketUserThreads userThreads) {
        super(telegramBot);
        this.userThreads = userThreads;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            @SneakyThrows
            public int process(List<Update> updates) {
                for (Update update : updates) {
                    userThreads.add(update);
                }

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
            log.error(e.getMessage(), e);
        });
    }
}
