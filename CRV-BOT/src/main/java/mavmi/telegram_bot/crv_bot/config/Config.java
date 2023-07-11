package mavmi.telegram_bot.crv_bot.config;

import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import mavmi.telegram_bot.crv_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
@PropertySource("classpath:database.properties")
public class Config {
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${db.driver.name}")
    private String dbDriver;

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot();
    }

    @Bean("DataSource")
    @Scope("singleton")
    public DriverManagerDataSource getDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriver);

        return dataSource;
    }

    @Bean("RequestOptions")
    @Scope("singleton")
    public RequestOptions getHttpData(){
        return new RequestOptions();
    }
}
