package mavmi.telegram_bot.rocketchat.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import mavmi.telegram_bot.common.telegramBot.TelegramBotSender;
import org.springframework.stereotype.Component;

@Component
public class RocketchatTelegramBotSender extends TelegramBotSender {

    public RocketchatTelegramBotSender(TelegramBot telegramBot) {
        super(telegramBot);
    }

}
