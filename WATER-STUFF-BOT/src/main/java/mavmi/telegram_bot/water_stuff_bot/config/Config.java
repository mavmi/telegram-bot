package mavmi.telegram_bot.water_stuff_bot.config;

import mavmi.telegram_bot.water_stuff_bot.telegram_bot.Bot;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot();
    }

}
