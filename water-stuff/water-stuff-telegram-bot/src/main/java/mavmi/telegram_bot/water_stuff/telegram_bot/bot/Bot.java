package mavmi.telegram_bot.water_stuff.telegram_bot.bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDice;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.bot.AbsTelegramBot;
import mavmi.telegram_bot.water_stuff.telegram_bot.http.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Bot extends AbsTelegramBot {

    private final HttpClient httpClient;

    public Bot(
            HttpClient httpClient,
            @Value("${bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.httpClient = httpClient;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                httpClient.processRequest(update.message());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            e.printStackTrace(System.err);
        });
    }

    public int sendDice(long chatId) {
        return telegramBot.execute(new SendDice(chatId))
                .message()
                .dice()
                .value();
    }
}
