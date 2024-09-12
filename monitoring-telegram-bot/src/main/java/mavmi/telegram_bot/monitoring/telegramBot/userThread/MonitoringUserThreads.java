package mavmi.telegram_bot.monitoring.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThreads;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.MonitoringDirectService;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonitoringUserThreads extends UserThreads<MonitoringUserThread> {

    private final MonitoringTelegramBotSender sender;
    private final MonitoringDirectService service;
    private final RequestsMapper requestsMapper;

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
            userThread = new MonitoringUserThread(this, service, requestsMapper, chatId, hostTarget);
            tgIdToUserThread.put(chatId, userThread);
            userThread.add(update);
            new Thread(userThread).start();
        } else {
            userThread.add(update);
        }
    }
}
