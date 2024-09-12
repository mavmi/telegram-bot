package mavmi.telegram_bot.rocketchat.telegramBot.client;

import com.pengrad.telegrambot.TelegramBot;
import mavmi.telegram_bot.common.telegramBot.client.TelegramBotSender;
import org.springframework.stereotype.Component;

@Component
public class RocketTelegramBotSender extends TelegramBotSender {

    public RocketTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

}
