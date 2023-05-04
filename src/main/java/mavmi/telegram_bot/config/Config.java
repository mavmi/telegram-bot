package mavmi.telegram_bot.config;

import mavmi.telegram_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Arrays;
import java.util.HashSet;

@Configuration
@PropertySource("classpath:telegram-bot.properties")
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Value("${bot_token}")
    private String botToken;

    @Value("${available_users}")
    private String[] availableUsers;

    @Bean("TelegramBot")
    public Bot getTelegramBot(){
        return new Bot(botToken, new HashSet<>(Arrays.asList(availableUsers)));
    }
}
