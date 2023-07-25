package mavmi.telegram_bot.shakal_bot.app;

import mavmi.telegram_bot.shakal_bot.config.Config;
import mavmi.telegram_bot.shakal_bot.telegram_bot.Bot;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args){
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Bot bot = (Bot)context.getBean("TelegramBot");
        mavmi.telegram_bot.common.header.ShakalBotEnterprisesHeader.printHeader();
        bot.run();
    }

}
