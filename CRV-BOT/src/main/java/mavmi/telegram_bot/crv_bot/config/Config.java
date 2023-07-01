package mavmi.telegram_bot.crv_bot.config;

import mavmi.telegram_bot.crv_bot.telegram_bot.Bot;
import mavmi.telegram_bot.crv_bot.telegram_bot.User;
import mavmi.telegram_bot.crv_bot.telegram_bot.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

@Configuration
@PropertySource("classpath:data.properties")
@PropertySource("classpath:headers.properties")
@PropertySource("classpath:body.properties")
public class Config {
    @Value("${url}")
    private String URL;
    @Value("${body}")
    private String body;
    @Value("${headers}")
    private String[] headers;
    @Value("${id}")
    private long[] id;
    @Value("${SI}")
    private String[] SI;
    @Value("${token}")
    private String[] token;

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot();
    }

    @Bean("Users")
    @Scope("singleton")
    public Users getUsers(){
        Users users = new Users();

        for (int i = 0; i < id.length; i++){
            users.add(new User(id[i], SI[i], token[i]));
        }

        return users;
    }

    @Bean("URL")
    @Scope("singleton")
    public String getUrl(){
        return URL;
    }

    @Bean("Headers")
    @Scope("singleton")
    public String[] getHeaders(){
        return headers;
    }
}
