package mavmi.telegram_bot.config;

import mavmi.telegram_bot.telegram_bot.Bot;
import mavmi.telegram_bot.telegram_bot.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot();
    }

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger();
    }
}
