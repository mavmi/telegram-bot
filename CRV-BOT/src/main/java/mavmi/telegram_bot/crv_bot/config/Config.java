package mavmi.telegram_bot.crv_bot.config;

import mavmi.telegram_bot.crv_bot.request.HttpData;
import mavmi.telegram_bot.crv_bot.request.HttpUtils;
import mavmi.telegram_bot.crv_bot.telegram_bot.Bot;
import mavmi.telegram_bot.crv_bot.user.User;
import mavmi.telegram_bot.crv_bot.user.Users;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@PropertySource("classpath:data.properties")
public class Config {
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

    @Bean("HttpData")
    @Scope("singleton")
    public HttpData getHttpData(){
        return new HttpData();
    }

    @Bean("HttpUtils")
    @Scope("singleton")
    public HttpUtils getHttpUtils(){
        return new HttpUtils();
    }
}
