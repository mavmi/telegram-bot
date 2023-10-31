package mavmi.telegram_bot.shakal_bot.telegram_bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.shakal_bot.constants.Phrases;
import mavmi.telegram_bot.shakal_bot.service.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class Bot extends AbsTelegramBot {
    @Getter
    private final static ReplyKeyboardMarkup diceKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);

    static {
        diceKeyboard.addRow(Phrases.DICE_THROW_MSG);
        diceKeyboard.addRow(Phrases.DICE_QUIT_MSG);
    }

    @Getter
    private final static ReplyKeyboardMarkup horoscopeKeyboard = new ReplyKeyboardMarkup(new String[]{})
            .oneTimeKeyboard(true)
            .resizeKeyboard(true);

    static {
        for (Map.Entry<String, String> entry : Phrases.HOROSCOPE_SIGNS.entrySet()) {
            horoscopeKeyboard.addRow(entry.getKey());
        }
    }

    public Bot(
            Service service,
            Logger logger,
            @Value("${bot.token}") String telegramBotToken
    ) {
        super(service, logger, telegramBotToken);
    }

    @Override
    @PostConstruct
    public void run() {
        logger.log("SHAKAL-BOT IS RUNNING");
        service.setTelegramBot(this);
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                service.handleRequest(update.message());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }
}
