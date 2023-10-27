package mavmi.telegram_bot.monitoring_bot.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:application.properties")
@SpringBootApplication(scanBasePackages = {
        "mavmi.telegram_bot.common",
        "mavmi.telegram_bot.monitoring_bot"
})
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
