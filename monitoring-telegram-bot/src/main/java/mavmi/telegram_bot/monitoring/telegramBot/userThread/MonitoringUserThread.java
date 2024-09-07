package mavmi.telegram_bot.monitoring.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThread;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.MonitoringDirectService;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.telegramBot.MonitoringTelegramBotSender;

import java.util.ArrayDeque;
import java.util.Queue;

@Slf4j
public class MonitoringUserThread implements UserThread {

    private final MonitoringUserThreads userThreads;
    private final RequestsMapper requestsMapper;
    private final MonitoringTelegramBotSender sender;
    private final MonitoringDirectService monitoringService;
    private final String hostTarget;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();

    public MonitoringUserThread(
            MonitoringUserThreads userThreads,
            MonitoringTelegramBotSender sender,
            MonitoringDirectService monitoringService,
            RequestsMapper requestsMapper,
            long chatId,
            String hostTarget
    ) {
        this.userThreads = userThreads;
        this.requestsMapper = requestsMapper;
        this.sender = sender;
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
            MonitoringServiceRs monitoringServiceRs = monitoringService.handleRequest(monitoringServiceRq);

            switch (monitoringServiceRs.getMonitoringServiceTask()) {
                case SEND_TEXT -> sendText(monitoringServiceRs);
                case SEND_TEXT_DELETE_KEYBOARD -> sendTextDeleteKeyboard(monitoringServiceRs);
                case SEND_KEYBOARD -> sendKeyboard(monitoringServiceRs);
            }
        }

        userThreads.removeThread(chatId);
    }

    private void sendText(MonitoringServiceRs response) {
        sender.sendText(chatId, response.getMessageJson().getTextMessage());
    }

    private void sendTextDeleteKeyboard(MonitoringServiceRs response) {
        sender.sendTextMessage(chatId, response.getMessageJson().getTextMessage(), new ReplyKeyboardRemove());
    }

    private void sendKeyboard(MonitoringServiceRs response) {
        sender.sendReplyKeyboard(
                chatId,
                response.getMessageJson().getTextMessage(),
                response.getReplyKeyboardJson().getKeyboardButtons()
        );
    }
}
