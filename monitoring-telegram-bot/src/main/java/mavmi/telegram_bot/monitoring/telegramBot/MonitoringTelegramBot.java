package mavmi.telegram_bot.monitoring.telegramBot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.monitoring.mapper.RequestsMapper;
import mavmi.telegram_bot.monitoring.service.MonitoringDirectService;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class MonitoringTelegramBot extends TelegramBot {

    private final RequestsMapper requestsMapper;
    private final MonitoringDirectService monitoringService;
    private final String hostTarget;

    public MonitoringTelegramBot(
            RequestsMapper requestsMapper,
            MonitoringDirectService monitoringService,
            @Value("${telegram-bot.token}") String telegramBotToken,
            @Value("${telegram-bot.task-target}") String hostTarget
    ){
        super(telegramBotToken);
        this.requestsMapper = requestsMapper;
        this.monitoringService = monitoringService;
        this.hostTarget = hostTarget;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            @SneakyThrows
            public int process(List<Update> updates) {
                for (Update update : updates) {
                    log.info("Got request from id {}", update.message().from().id());

                    Message telegramMessage = update.message();
                    Long chatId = telegramMessage.from().id();
                    String msg = telegramMessage.text();
                    if (msg == null) {
                        log.info("Message is null");
                        continue;
                    }

                    MonitoringServiceRq monitoringServiceRq = requestsMapper.telegramMessageToMonitoringServiceRequest(telegramMessage, hostTarget);

                    CompletableFuture
                            .supplyAsync(() -> {
                                return monitoringService.handleRequest(monitoringServiceRq);
                            }).thenApply(arg -> {
                                MonitoringServiceRs monitoringServiceRs = (MonitoringServiceRs) arg;
                                if (monitoringServiceRs == null) {
                                    return null;
                                }

                                switch (monitoringServiceRs.getMonitoringServiceTask()) {
                                    case SEND_TEXT -> sendText(chatId, monitoringServiceRs.getMessageJson().getTextMessage());
                                    case SEND_KEYBOARD -> sendReplyKeyboard(
                                            chatId,
                                            monitoringServiceRs.getMessageJson().getTextMessage(),
                                            monitoringServiceRs.getReplyKeyboardJson().getKeyboardButtons()
                                    );
                                }
                                return monitoringServiceRq;
                            }).join();
                }
                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, new ExceptionHandler() {
            @Override
            public void onException(TelegramException e) {
                e.printStackTrace(System.out);
            }
        });
    }

    public void sendText(long chatId, String msg) {
        sendText(List.of(chatId), msg);
    }

    public void sendText(List<Long> chatIdx, String msg) {
        for (Long chatId : chatIdx) {
            sendMessage(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));
        }
    }

    public void sendReplyKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sendReplyKeyboard(List.of(chatId), msg, keyboardButtons);
    }

    public void sendReplyKeyboard(List<Long> chatIdx, String msg, String[] keyboardButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : keyboardButtons) {
            replyKeyboardMarkup.addRow(button);
        }

        for (Long chatId : chatIdx) {
            sendMessage(new SendMessage(chatId, msg)
                    .replyMarkup(replyKeyboardMarkup)
                    .parseMode(ParseMode.Markdown));
        }
    }

    public void sendFile(List<Long> chatIdx, File file){
        for (Long id : chatIdx){
            sendRequest(new SendDocument(id, file));
        }
    }
}
