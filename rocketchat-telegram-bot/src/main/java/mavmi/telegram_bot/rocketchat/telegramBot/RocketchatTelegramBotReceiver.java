package mavmi.telegram_bot.rocketchat.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.TelegramBotReceiver;
import mavmi.telegram_bot.rocketchat.telegramBot.userThread.RocketchatUserThreads;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class RocketchatTelegramBotReceiver extends TelegramBotReceiver {

    private final RocketchatUserThreads userThreads;

    public RocketchatTelegramBotReceiver(
            TelegramBot telegramBot,
            RocketchatUserThreads userThreads
    ) {
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
