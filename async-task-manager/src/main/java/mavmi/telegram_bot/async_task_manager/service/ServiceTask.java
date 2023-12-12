package mavmi.telegram_bot.async_task_manager.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceTask {
    private String target;
    private String message;
}
