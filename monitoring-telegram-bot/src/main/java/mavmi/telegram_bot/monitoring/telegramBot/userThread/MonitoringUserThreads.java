package mavmi.telegram_bot.monitoring.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.telegram_bot_starter.userThread.UserThreads;
import mavmi.telegram_bot.lib.user_cache_starter.provider.UserCachesProvider;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.monitoring.MonitoringService;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringUserThreads extends UserThreads<MonitoringUserThread> {

    private final MonitoringTelegramBotSender sender;
    private final MonitoringService service;
    private final RequestsMapper requestsMapper;
    private final UserCachesProvider userCachesProvider;

    @Value("${telegram-bot.task-target}")
    private String hostTarget;

    @Override
    public void add(Update update) {
        Message message = update.message();
        if (message == null) {
            return;
        }

        long chatId = message.chat().id();
        MonitoringUserThread userThread = (MonitoringUserThread) tgIdToUserThread.get(chatId);

        if (userThread == null) {
            userThread = new MonitoringUserThread(this, userCachesProvider, requestsMapper, service, hostTarget, chatId);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            new Thread(userThread).start();
        } else {
            userThread.add(update);
        }
    }
}
