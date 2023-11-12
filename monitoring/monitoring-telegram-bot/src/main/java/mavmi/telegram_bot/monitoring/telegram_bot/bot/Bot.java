package mavmi.telegram_bot.monitoring.telegram_bot.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.bot.AbsTelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
public class Bot extends AbsTelegramBot {

    public Bot(@Value("${telegram-bot.token}") String telegramBotToken){
        super(telegramBotToken);
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(
                updates -> {
                    for (Update update : updates) {
                        log.info("Got request from id {}", update.message().from().id());
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
