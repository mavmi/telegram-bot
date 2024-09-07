package mavmi.telegram_bot.monitoring.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import mavmi.telegram_bot.common.telegramBot.TelegramBotReceiver;
import mavmi.telegram_bot.monitoring.telegramBot.userThread.MonitoringUserThreads;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MonitoringTelegramBotReceiver extends TelegramBotReceiver {

    private final MonitoringUserThreads userThreads;

    public MonitoringTelegramBotReceiver(
            TelegramBot telegramBot,
            MonitoringUserThreads userThreads
    ) {
        super(telegramBot);
        this.userThreads = userThreads;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            public int process(List<Update> list) {
                for (Update update : list) {
                    userThreads.add(update);
                }

                return CONFIRMED_UPDATES_ALL;
            }
        });
    }
}
