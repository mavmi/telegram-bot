package mavmi.telegram_bot.common.telegramBot;

import com.pengrad.telegrambot.TelegramBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelegramBotConfiguration {

    @Bean("telegramBot")
    public TelegramBot getTelegramBot(@Value("${telegram-bot.token}") String token) {
        return new TelegramBot(token);
    }
}
