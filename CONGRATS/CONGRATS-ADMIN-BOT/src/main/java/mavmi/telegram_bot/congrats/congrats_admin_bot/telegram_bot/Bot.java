package mavmi.telegram_bot.congrats.congrats_admin_bot.telegram_bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.service.AbsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class Bot extends AbsTelegramBot {
    public Bot(
            AbsService service,
            @Value("${bot.token}") String botToken
    ) {
        super(service, botToken);
    }

    @Override
    @PostConstruct
    public void run() {
        log.info("CONGRATS-ADMIN-BOT IS RUNNING");
        service.setTelegramBot(this);
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                service.handleRequest(update.message());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            e.printStackTrace(System.err);
        });
    }

    public String getFileUrl(String fileId) {
        GetFile getFile = new GetFile(fileId);
        GetFileResponse getFileResponse = telegramBot.execute(getFile);

        return telegramBot.getFullFilePath(getFileResponse.file());
    }
}
