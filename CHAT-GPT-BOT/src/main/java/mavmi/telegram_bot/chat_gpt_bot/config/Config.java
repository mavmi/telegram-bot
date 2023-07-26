package mavmi.telegram_bot.chat_gpt_bot.config;

import mavmi.telegram_bot.chat_gpt_bot.telegram_bot.Bot;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.logger.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("mavmi.telegram_bot.chat_gpt_bot")
@PropertySource("classpath:application.properties")
@Import(mavmi.telegram_bot.common.config.Configuration.class)
public class Config {
    @Value("${telegramBotToken}")
    private String telegramBotToken;
    @Value("${chatGptToken}")
    private String chatGptToken;
    @Value("${logFile}")
    private String logFile;

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger(logFile);
    }

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(UserAuthentication userAuthentication, Logger logger){
        return new Bot(
                telegramBotToken,
                chatGptToken,
                logger,
                userAuthentication
        );
    }

}
