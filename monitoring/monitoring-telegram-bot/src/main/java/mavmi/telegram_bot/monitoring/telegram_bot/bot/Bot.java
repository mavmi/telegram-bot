package mavmi.telegram_bot.monitoring.telegram_bot.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.bot.AbsTelegramBot;
import mavmi.telegram_bot.monitoring.telegram_bot.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.List;

@Slf4j
@Component
public class Bot extends AbsTelegramBot {

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
        telegramBot.setUpdatesListener(
                updates -> {
                    for (Update update : updates) {
                        log.info("Got request from id {}", update.message().from().id());

                        Long id = update.message().from().id();
                        String msg = update.message().text();
                        if (msg == null) {
                            log.info("Message is null");
                            continue;
                        }

                        int code = httpClient.putTask(id, hostTarget, msg);

                        if (code != HttpURLConnection.HTTP_OK) {
                            long chatId = update.message().from().id();
                            this.sendMessage(chatId, "Service unavailable");
                        }
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                }, e -> {
                    e.printStackTrace(System.out);
                }
        );
    }

    synchronized public void sendMessage(List<Long> chatIdx, String msg){
        for (Long id : chatIdx){
            try {
                sendMessage(id, msg);
                log.info("Message sent to id: {}", id);
            } catch (RuntimeException e){
                e.printStackTrace(System.out);
            }
        }
    }

    synchronized public void sendFile(List<Long> chatIdx, File file){
        for (Long id : chatIdx){
            try {
                sendRequest(new SendDocument(id, file));
                log.info("File sent to id: {}", id);
            } catch (RuntimeException e){
                e.printStackTrace(System.out);
            }
        }
    }
}
