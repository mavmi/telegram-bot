package mavmi.telegram_bot.monitoring.cache.inner.dataCache;

import lombok.Getter;
import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;

import java.util.List;

@Getter
public class UserPrivileges {

    private final List<PRIVILEGE> privileges;

    public UserPrivileges(List<PRIVILEGE> privileges) {
        this.privileges = privileges;
    }

    public boolean contains(String privilegeName) {
        return privileges.stream().anyMatch(privilege -> privilege.getName().equals(privilegeName));
    }

    public boolean contains(PRIVILEGE privilege) {
        return privileges.contains(privilege);
    }
}
