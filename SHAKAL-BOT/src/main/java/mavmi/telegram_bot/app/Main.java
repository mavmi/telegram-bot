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

import javax.sql.DataSource;

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

        Logger logger = (Logger)context.getBean("Logger");
        Bot bot = (Bot)context.getBean("TelegramBot");

        logger.setDataSource((DataSource)context.getBean("DataSource"))
                .setLogFile(parsedArgs.getLogFile());
        bot.setLogger(logger)
                .setToken(parsedArgs.getToken());

        bot.run();
    }
}
