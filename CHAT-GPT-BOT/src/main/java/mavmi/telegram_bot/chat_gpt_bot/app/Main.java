package mavmi.telegram_bot.chat_gpt_bot.app;

import mavmi.telegram_bot.chat_gpt_bot.args.Args;
import mavmi.telegram_bot.chat_gpt_bot.args.ArgsException;
import mavmi.telegram_bot.chat_gpt_bot.config.Config;
import mavmi.telegram_bot.chat_gpt_bot.telegram_bot.Bot;
import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.common.user_authentication.AvailableUsers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
                .setChatGptToken(parsedArgs.getChatGptToken())
                .setLogger(Logger.getInstance())
                .setAvailableUsers((AvailableUsers) context.getBean("AvailableUsers"));

        mavmi.telegram_bot.common.header.ShakalBotEnterprisesHeader.printHeader();
        bot.run();
    }
}
