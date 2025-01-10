package mavmi.telegram_bot.monitoring.cache.inner.dataCache;

import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;

import java.util.List;

public class Privileges {

    private final List<PRIVILEGE> privileges;

    public Privileges(List<PRIVILEGE> privileges) {
        this.privileges = privileges;
    }

    public boolean contains(String privilegeName) {
        return privileges.stream().anyMatch(privilege -> privilege.getName().equals(privilegeName));
    }

    public boolean contains(PRIVILEGE privilege) {
        return privileges.contains(privilege);
    }
}
