package mavmi.telegram_bot.monitoring.cache.inner.dataCache;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;

import java.util.List;

@Getter
@Setter
@Accessors(chain = true)
public class PrivilegesManagement {
    private long workingTelegramId;
    private List<PRIVILEGE> workingPrivileges;
}
