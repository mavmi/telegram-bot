package mavmi.telegram_bot.app;

import mavmi.telegram_bot.config.Config;
import mavmi.telegram_bot.telegram_bot.Bot;
import mavmi.telegram_bot.telegram_bot.Logger;
import mavmi.telegram_bot.utils.Args;
import mavmi.telegram_bot.utils.ArgsException;
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

        Args parsedArgs = null;
        try {
            parsedArgs = new Args(args);
        } catch (ArgsException e){
            System.err.println(e.getMessage());
            System.exit(1);
        }

        Bot bot = (Bot)context.getBean("TelegramBot");
        bot.setTelegramBot(parsedArgs.getBotToken())
                .setLogger(((Logger)context.getBean("Logger")).setLogFile(parsedArgs.getLogFile()))
                .setWaterContainer(parsedArgs.getWorkingFile())
                .setAvailableUser(parsedArgs.getAvailableUser());

        System.out.println("Powered by " + "\033[0;1m" + "Shakal Bot Enterprises" + "\033[0m");
        bot.run();
    }
}
