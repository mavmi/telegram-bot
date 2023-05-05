package mavmi.telegram_bot.config;

import mavmi.telegram_bot.telegram_bot.Bot;
import mavmi.telegram_bot.telegram_bot.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:telegram-bot.properties")
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Value("${bot_token}")
    private String botToken;
    @Value("${available_users}")
    private String[] availableUsers;
    @Value("${log_file}")
    private String logFilePath;

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot(botToken, availableUsers, getLogger());
    }

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger(logFilePath);
    }
}
