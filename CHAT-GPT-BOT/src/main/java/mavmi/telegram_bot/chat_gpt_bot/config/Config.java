package mavmi.telegram_bot.chat_gpt_bot.config;

import mavmi.telegram_bot.chat_gpt_bot.telegram_bot.Bot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot();
    }

}
