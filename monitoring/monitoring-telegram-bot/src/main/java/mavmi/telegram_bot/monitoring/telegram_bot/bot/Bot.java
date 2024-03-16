package mavmi.telegram_bot.monitoring.telegram_bot.bot;

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
import mavmi.telegram_bot.common.bot.AbstractTelegramBot;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.telegram_bot.httpClient.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class Bot extends AbstractTelegramBot {

    private final HttpClient httpClient;
    private final String hostTarget;

    public Bot(
            HttpClient httpClient,
            @Value("${telegram-bot.token}") String telegramBotToken,
            @Value("${telegram-bot.task-target}") String hostTarget
    ){
        super(telegramBotToken);
        this.httpClient = httpClient;
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

                   ResponseEntity<MonitoringServiceRs> response = httpClient.monitoringServiceRequest(telegramMessage, hostTarget);
                   if (response.getStatusCode().equals(HttpStatusCode.valueOf(HttpStatus.OK.value()))) {
                       MonitoringServiceRs monitoringServiceRs = response.getBody();
                       switch (monitoringServiceRs.getMonitoringServiceTask()) {
                           case SEND_TEXT -> sendText(chatId, monitoringServiceRs.getMessageJson().getTextMessage());
                           case SEND_KEYBOARD -> sendKeyboard(
                                   chatId,
                                   monitoringServiceRs.getMessageJson().getTextMessage(),
                                   monitoringServiceRs.getKeyboardJson().getKeyboardButtons()
                           );
                       }
                   } else {
                       sendText(chatId, "Service unavailable");
                   }
               }
               return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
            e.printStackTrace(System.out);
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

    public void sendKeyboard(long chatId, String msg, String[] keyboardButtons) {
        sendKeyboard(List.of(chatId), msg, keyboardButtons);
    }

    public void sendKeyboard(List<Long> chatIdx, String msg, String[] keyboardButtons) {
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
