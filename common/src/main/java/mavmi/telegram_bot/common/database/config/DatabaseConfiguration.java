package mavmi.telegram_bot.common.database.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("mavmi.telegram_bot.common")
public class DatabaseConfiguration {

    @Bean("DataSource")
    @ConditionalOnProperty(
            prefix = "db",
            name = {
                    "url",
                    "username",
                    "password",
                    "driver.name"
            }
    )
    public DataSource getPromDataSource(
            @Value("${db.url}") String dbUrl,
            @Value("${db.username}") String dbUsername,
            @Value("${db.password}") String dbPassword,
            @Value("${db.driver.name}") String dbDriver
    ){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriver);

        return dataSource;
    }
}
