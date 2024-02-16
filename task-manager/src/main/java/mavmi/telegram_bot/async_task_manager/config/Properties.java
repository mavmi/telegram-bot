package mavmi.telegram_bot.async_task_manager.config;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.common.taskManager.TASK_MANAGER_RQ_TYPE;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "service")
public class Properties {
    private String shell;
    private Map<String, String> taskToScript;
    private Map<String, TASK_MANAGER_RQ_TYPE> taskToType;
}
