package mavmi.telegram_bot.crv_bot.app;

import mavmi.telegram_bot.crv_bot.args.Args;
import mavmi.telegram_bot.crv_bot.args.ArgsException;
import mavmi.telegram_bot.crv_bot.config.Config;
import mavmi.telegram_bot.crv_bot.request.RequestOptions;
import mavmi.telegram_bot.crv_bot.telegram_bot.Bot;
import mavmi.telegram_bot.utils.logger.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.sql.DataSource;

public class Main {
    public static void main(String[] args){
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
                .setLogger(Logger.getInstance())
                .setRequestOptions((RequestOptions) context.getBean("RequestOptions"))
                .setDataSource((DataSource) context.getBean("DataSource"));

        mavmi.telegram_bot.utils.header.ShakalBotEnterprisesHeader.printHeader();
        bot.run();
    }
}
