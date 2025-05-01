package mavmi.telegram_bot.lib.database_starter.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("mavmi.telegram_bot.lib.database_starter.*")
@EntityScan("mavmi.telegram_bot.lib.database_starter.*")
@EnableJpaRepositories("mavmi.telegram_bot.lib.database_starter.*")
public class DatabaseConfiguration {

}
