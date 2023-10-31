package mavmi.telegram_bot.water_stuff_bot.telegram_bot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import jakarta.annotation.PostConstruct;
import mavmi.telegram_bot.common.bot.AbsTelegramBot;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.service.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static mavmi.telegram_bot.water_stuff_bot.constants.Buttons.NO_BTN;
import static mavmi.telegram_bot.water_stuff_bot.constants.Buttons.YES_BTN;

@Component
public class Bot extends AbsTelegramBot {
    private final NotificationThread notificationThread;

    public Bot(
            Service service,
            NotificationThread notificationThread,
            Logger logger,
            @Value("${bot.token}") String telegramBotToken
    ) {
        super(service, logger, telegramBotToken);
        this.notificationThread = notificationThread;
    }

    @Override
    @PostConstruct
    public void run() {
        logger.log("WATER-STUFF-BOT IS RUNNING");

        service.setTelegramBot(this);
        notificationThread.setTelegramBot(this);
        notificationThread.start();

        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                service.handleRequest(update.message());
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            logger.err(e.getMessage());
        });
    }

    public ReplyKeyboardMarkup generateGroupsKeyboard(String[] names) {
        KeyboardButton[] buttons = new KeyboardButton[names.length];

        for (int i = 0; i < names.length; i++) {
            buttons[i] = new KeyboardButton(names[i]);
        }

        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
    }

    public ReplyKeyboardMarkup generateApproveKeyboard() {
        KeyboardButton[] buttons = new KeyboardButton[]{
                new KeyboardButton(YES_BTN),
                new KeyboardButton(NO_BTN)
        };

        return new ReplyKeyboardMarkup(buttons).resizeKeyboard(true).oneTimeKeyboard(true);
    }
}
