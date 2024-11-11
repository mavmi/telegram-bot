package mavmi.telegram_bot.rocketchat;

import mavmi.parameters_management_system.client.config.PmsClientConfiguration;
import mavmi.telegram_bot.common.config.CommonConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

@EnableConfigurationProperties
@ConfigurationPropertiesScan
@SpringBootApplication
@Import({CommonConfig.class, PmsClientConfiguration.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
