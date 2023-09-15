package mavmi.telegram_bot.common.config;

import mavmi.telegram_bot.common.argument.ArgsParser;
import mavmi.telegram_bot.common.auth.UserAuthentication;
import mavmi.telegram_bot.common.database.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@org.springframework.context.annotation.Configuration
@ComponentScan("mavmi.telegram_bot.common")
@PropertySource({"classpath:database.properties"})
public class Configuration {
    @Value("${db.url}")
    private String dbUrl;
    @Value("${db.username}")
    private String dbUsername;
    @Value("${db.password}")
    private String dbPassword;
    @Value("${db.driver.name}")
    private String dbDriver;

    @Bean("DataSource")
    public DataSource getDataSource(){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriver);

        return dataSource;
    }

    @Bean("CrvRepository")
    public CrvRepository getCrvRepository(DataSource dataSource){
        return new CrvRepository(dataSource);
    }

    @Bean("RequestRepository")
    public RequestRepository getRequestRepository(DataSource dataSource){
        return new RequestRepository(dataSource);
    }

    @Bean("RuleRepository")
    public RuleRepository getRuleRepository(DataSource dataSource){
        return new RuleRepository(dataSource);
    }

    @Bean("UserRepository")
    public UserRepository getUserRepository(DataSource dataSource){
        return new UserRepository(dataSource);
    }

    @Bean("WaterStuffRepository")
    public WaterStuffRepository getWaterStuffRepository(DataSource dataSource){
        return new WaterStuffRepository(dataSource);
    }

    @Bean("RockerUserRepository")
    public RocketUserRepository getRockerUserRepository(DataSource dataSource){
        return new RocketUserRepository(dataSource);
    }

    @Bean("UserAuthentication")
    public UserAuthentication getUserAuthentication(RuleRepository ruleRepository){
        return new UserAuthentication(ruleRepository);
    }

    @Bean("ArgsParser")
    public ArgsParser getArgsParser(){
        return new ArgsParser();
    }
}
