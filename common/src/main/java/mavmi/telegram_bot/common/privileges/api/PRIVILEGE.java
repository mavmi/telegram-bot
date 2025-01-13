package mavmi.telegram_bot.common.privileges.api;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.lang.Nullable;

@AllArgsConstructor
public enum PRIVILEGE {
    PMS("pms"),
    BOT_ACCESS("bot-access"),
    CERT_GENERATION("cert-generation"),
    PRIVILEGES("privileges"),
    SERVER_INFO("server-info"),
    APPS("apps");

    @Getter
    private final String name;

    @Nullable
    public static PRIVILEGE findByName(String name) {
        for (PRIVILEGE privilege : PRIVILEGE.values()) {
            if (privilege.getName().equals(name)) {
                return privilege;
            }
        }

        return null;
    }
}
