package mavmi.telegram_bot.rocket_bot.config;

import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.repository.RocketGroupsRepository;
import mavmi.telegram_bot.common.database.repository.RocketImRepository;
import mavmi.telegram_bot.common.database.repository.RocketUserRepository;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.rocket_bot.httpHandler.HttpHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.handler.JsonHandler;
import mavmi.telegram_bot.rocket_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource({"classpath:application.properties"})
@ComponentScan("mavmi.telegram_bot.rocket_bot")
@Import(mavmi.telegram_bot.common.config.Configuration.class)
public class Config {
    @Value("${telegramBotToken}")
    private String telegramBotToken;
    @Value("${sleepTime}")
    private Long sleepTime;
    @Value("${logFile}")
    private String logFile;
    @Value("${adminId}")
    private Long adminId;
    @Value("${loginUrl}")
    private String loginUrl;
    @Value("${meUrl}")
    private String meUrl;
    @Value("${imListUrl}")
    private String imListUrl;
    @Value("${imHistoryUrl}")
    private String imHistoryUrl;
    @Value("${groupsListUrl}")
    private String groupsListUrl;
    @Value("${groupsHistoryUrl}")
    private String groupsHistoryUrl;
    @Value("${hostUrl}")
    private String hostUrl;
    @Value("${rcUidHeader}")
    private String rcUidHeader;
    @Value("${rcTokenHeader}")
    private String rcTokenHeader;

    @Bean("JsonHandler")
    @Scope("singleton")
    public JsonHandler getJsonHandler() {
        return new JsonHandler();
    }

    @Bean("HttpHandler")
    @Scope("singleton")
    public HttpHandler getHttpHandler(JsonHandler jsonHandler) {
        return new HttpHandler(
                jsonHandler,
                loginUrl,
                meUrl,
                imListUrl,
                imHistoryUrl,
                groupsListUrl,
                groupsHistoryUrl,
                rcUidHeader,
                rcTokenHeader,
                hostUrl
        );
    }

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger(logFile);
    }

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(
            UserAuthentication userAuthentication,
            RocketUserRepository rocketUserRepository,
            RocketImRepository rocketImRepository,
            RocketGroupsRepository rocketGroupsRepository,
            HttpHandler httpHandler,
            Logger logger
    ) {
        return new Bot(
                telegramBotToken,
                sleepTime,
                userAuthentication,
                rocketUserRepository,
                rocketImRepository,
                rocketGroupsRepository,
                httpHandler,
                logger,
                adminId
        );
    }
}
