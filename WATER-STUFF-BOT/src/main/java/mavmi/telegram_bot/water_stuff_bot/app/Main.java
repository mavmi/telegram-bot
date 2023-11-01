package mavmi.telegram_bot.water_stuff_bot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "mavmi.telegram_bot.common",
        "mavmi.telegram_bot.water_stuff_bot"
})
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }
}
