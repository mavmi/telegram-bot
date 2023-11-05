package mavmi.telegram_bot.congrats.congrats_admin_bot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "mavmi.telegram_bot.common",
        "mavmi.telegram_bot.congrats.utils",
        "mavmi.telegram_bot.congrats.congrats_admin_bot"
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
