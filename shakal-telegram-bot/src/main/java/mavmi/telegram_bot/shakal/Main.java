package mavmi.telegram_bot.shakal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "mavmi.telegram_bot.shakal",
        "mavmi.telegram_bot.common"
})
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

}
