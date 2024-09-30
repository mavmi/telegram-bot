package mavmi.telegram_bot.monitoring;

import mavmi.parameters_management_system.client.config.PmsClientConfiguration;
import mavmi.telegram_bot.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({CommonConfig.class, PmsClientConfiguration.class})
public class Main {

    public static void main(String[] args){
        SpringApplication.run(Main.class, args);
    }
}
