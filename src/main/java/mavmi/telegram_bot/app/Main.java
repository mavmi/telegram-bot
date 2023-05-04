package mavmi.telegram_bot.app;

import mavmi.telegram_bot.config.Config;
import mavmi.telegram_bot.telegram_bot.Bot;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@SpringBootApplication
public class Main implements ApplicationRunner {
    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        ((Bot)context.getBean("TelegramBot")).run();
    }
}
