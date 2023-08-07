package mavmi.telegram_bot.monitoring_bot.app;

import mavmi.telegram_bot.monitoring_bot.telegram_bot.Bot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("mavmi.telegram_bot.monitoring_bot")
public class Main {
    public static void main(String[] args){
        ApplicationContext context = SpringApplication.run(Main.class, args);
        Bot bot = (Bot)context.getBean("TelegramBot");
        mavmi.telegram_bot.common.header.ShakalBotEnterprisesHeader.printHeader();
        bot.run();
    }
}
