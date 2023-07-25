package mavmi.telegram_bot.crv_bot.config;

import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.repository.CrvRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import mavmi.telegram_bot.crv_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("mavmi.telegram_bot.crv_bot")
@PropertySource("classpath:application.properties")
@Import(mavmi.telegram_bot.common.config.Configuration.class)
public class Config {
    @Value("${telegramBotToken}")
    private String telegramBotToken;
    @Value("${logFile}")
    private String logFile;

    @Bean("RequestOptions")
    @Scope("singleton")
    public RequestOptions getHttpData(){
        return new RequestOptions();
    }

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger(logFile);
    }

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(RequestOptions requestOptions, UserAuthentication userAuthentication, CrvRepository crvRepository, Logger logger){
        return new Bot(telegramBotToken, logger, requestOptions, userAuthentication, crvRepository);
    }
}
