package mavmi.telegram_bot.shakal_bot.config;

import mavmi.telegram_bot.common.database.repository.RequestRepository;
import mavmi.telegram_bot.common.database.repository.UserRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.shakal_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource({"classpath:application.properties"})
@ComponentScan("mavmi.telegram_bot.shakal_bot")
@Import(mavmi.telegram_bot.common.config.Configuration.class)
public class Config {
    @Value("${telegramBotToken}")
    private String telegramBotToken;
    @Value("${logFile}")
    private String logFile;

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger(logFile);
    }

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(UserRepository userRepository, RequestRepository requestRepository, Logger logger){
        return new Bot(telegramBotToken, userRepository, requestRepository, logger);
    }
}
