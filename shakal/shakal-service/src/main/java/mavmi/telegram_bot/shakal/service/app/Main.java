package mavmi.telegram_bot.shakal.service.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "mavmi.telegram_bot.common",
        "mavmi.telegram_bot.shakal.service"
})
public class Main {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

}
