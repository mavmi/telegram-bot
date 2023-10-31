package mavmi.telegram_bot.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.sql.DataSource;

@org.springframework.context.annotation.Configuration
@ComponentScan("mavmi.telegram_bot.common")
public class Configuration {
    @Bean("TextEncryptor")
    public TextEncryptor getTextEncryptor(
            @Value("${security.key}") String key,
            @Value("${security.salt}") String salt
    ) {
        return Encryptors.text(key, salt);
    }

    @Bean("DataSource")
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
