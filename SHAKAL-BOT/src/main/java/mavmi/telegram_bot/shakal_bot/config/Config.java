package mavmi.telegram_bot.shakal_bot.config;

import mavmi.telegram_bot.shakal_bot.app.Main;
import mavmi.telegram_bot.shakal_bot.telegram_bot.Bot;
import mavmi.telegram_bot.utils.file.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

@Configuration
@PropertySource({"classpath:database.properties"})
@ComponentScan("mavmi.telegram_bot")
public class Config {
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${db.driver.name}")
    private String dbDriver;
    @Value("${db.timeout}")
    private int dbTimeout;

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

         try {
          dataSource
                  .getConnection()
                  .createStatement()
                  .executeUpdate(File.readFile(Main.class.getResourceAsStream("/database.sql")));
        } catch (SQLException e) {
          System.err.println(e.getMessage());
        }

        return dataSource;
     }
}
