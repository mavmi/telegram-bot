package mavmi.telegram_bot.common.database.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@ComponentScan("mavmi.telegram_bot.common.*")
@EntityScan("mavmi.telegram_bot.common.*")
@EnableJpaRepositories("mavmi.telegram_bot.common.*")
public class DatabaseConfiguration {

}
