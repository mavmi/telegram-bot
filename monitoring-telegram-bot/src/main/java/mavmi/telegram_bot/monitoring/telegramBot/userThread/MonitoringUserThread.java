package mavmi.telegram_bot.monitoring.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThread;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.MonitoringDirectService;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
public class MonitoringUserThread implements UserThread {

    private final MonitoringUserThreads userThreads;
    private final RequestsMapper requestsMapper;
    private final MonitoringDirectService monitoringService;
    private final String hostTarget;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();

    public MonitoringUserThread(
            MonitoringUserThreads userThreads,
            MonitoringDirectService monitoringService,
            RequestsMapper requestsMapper,
            long chatId,
            String hostTarget
    ) {
        this.userThreads = userThreads;
        this.requestsMapper = requestsMapper;
        this.monitoringService = monitoringService;
        this.hostTarget = hostTarget;
        this.chatId = chatId;
    }


    @Override
    public void add(Update update) {
        updateQueue.add(update);
    }

    @Override
    public void run() {
        while (!updateQueue.isEmpty()) {
            Message message = updateQueue.remove().message();
            log.info("Got request from id {}", message.from().id());

            String msg = message.text();
            if (msg == null) {
                log.info("Message is null");
                continue;
            }

            MonitoringServiceRq monitoringServiceRq = requestsMapper.telegramMessageToMonitoringServiceRequest(message, hostTarget);
            monitoringService.handleRequest(monitoringServiceRq);
        }

        userThreads.removeThread(chatId);
    }
}
