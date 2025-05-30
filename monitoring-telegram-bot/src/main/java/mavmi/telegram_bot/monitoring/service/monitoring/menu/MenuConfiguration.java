package mavmi.telegram_bot.monitoring.service.monitoring.menu;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class MenuConfiguration {

    @Bean
    public List<Menu> getMenuList() {
        return Arrays.asList(MonitoringServiceMenu.values());
    }
}
