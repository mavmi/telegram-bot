package mavmi.telegram_bot.water_stuff_bot.config;

import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.repository.RuleRepository;
import mavmi.telegram_bot.common.database.repository.WaterStuffRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("mavmi.telegram_bot.water_stuff_bot")
@PropertySource("classpath:application.properties")
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
    public Bot getTelegramBot(UserAuthentication userAuthentication, RuleRepository ruleRepository, WaterStuffRepository waterStuffRepository, Logger logger){
        return new Bot(telegramBotToken, logger, userAuthentication, ruleRepository, waterStuffRepository);
    }
}
