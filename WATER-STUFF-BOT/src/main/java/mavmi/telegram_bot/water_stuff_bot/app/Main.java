package mavmi.telegram_bot.water_stuff_bot.app;

import mavmi.telegram_bot.utils.user_authentication.AvailableUsers;
import mavmi.telegram_bot.water_stuff_bot.config.Config;
import mavmi.telegram_bot.water_stuff_bot.telegram_bot.Bot;
import mavmi.telegram_bot.utils.logger.Logger;
import mavmi.telegram_bot.water_stuff_bot.args.Args;
import mavmi.telegram_bot.water_stuff_bot.args.ArgsException;
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

        Logger.init(parsedArgs.getLogFile());
        Bot bot = (Bot)context.getBean("TelegramBot");

        bot.setTelegramBot(parsedArgs.getBotToken())
                .setLogger()
                .setWaterContainer(parsedArgs.getWorkingFile())
                .setAvailableUsers((AvailableUsers) context.getBean("AvailableUsers"));

        mavmi.telegram_bot.utils.header.ShakalBotEnterprisesHeader.printHeader();
        bot.run();
    }
}
