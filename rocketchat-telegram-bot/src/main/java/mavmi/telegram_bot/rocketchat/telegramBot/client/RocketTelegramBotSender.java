package mavmi.telegram_bot.rocketchat.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import mavmi.telegram_bot.lib.telegram_bot_starter.client.TelegramBotSender;
import org.springframework.stereotype.Component;

@Component
public class RocketTelegramBotSender extends TelegramBotSender {

    public RocketTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

}
