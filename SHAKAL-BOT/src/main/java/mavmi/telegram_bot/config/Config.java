package mavmi.telegram_bot.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mavmi.telegram_bot.telegram_bot.Bot;
import mavmi.telegram_bot.telegram_bot.Logger;
import mavmi.telegram_bot.utils.Utils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@PropertySource({"classpath:telegram-bot.properties", "classpath:database.properties"})
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Value("${bot_token}")
    private String botToken;
    @Value("${log_file}")
    private String logFilePath;

    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${db.driver}")
    private String dbDriver;
    @Value("${db.timeout}")
    private long dbTimeout;

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot(botToken, getLogger());
    }

    @Bean("Logger")
    @Scope("singleton")
    public Logger getLogger(){
        return new Logger(logFilePath, getDataSource());
    }

    @Bean("DataSource")
    @Scope("singleton")
    public DataSource getDataSource(){
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUsername);
        config.setPassword(dbPassword);
        config.setDriverClassName(dbDriver);
        config.setConnectionTimeout(dbTimeout);

        DataSource dataSource = new HikariDataSource(config);
        try {
            dataSource.getConnection().createStatement().executeUpdate(Utils.readFile(mavmi.telegram_bot.app.Main.class.getResourceAsStream("/database.sql")));
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return dataSource;
    }
}
