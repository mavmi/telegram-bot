package mavmi.telegram_bot.water_stuff_bot.config;

import mavmi.telegram_bot.utils.user_authentication.AvailableUsers;
import mavmi.telegram_bot.utils.user_authentication.UserInfo;
import mavmi.telegram_bot.water_stuff_bot.telegram_bot.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

@Configuration
@ComponentScan("mavmi.telegram_bot")
@PropertySource("classpath:available-users.properties")
public class Config {
    @Value("${id}")
    private Long[] idx;

    @Bean("TelegramBot")
    @Scope("singleton")
    public Bot getTelegramBot(){
        return new Bot();
    }

    @Bean("AvailableUsers")
    @Scope("singleton")
    public AvailableUsers getAvailableUsers(){
        AvailableUsers availableUsers = new AvailableUsers();

        for (int i = 0; i < idx.length; i++){
            availableUsers.addUser(new UserInfo(idx[i]));
        }

        return availableUsers;
    }

}
