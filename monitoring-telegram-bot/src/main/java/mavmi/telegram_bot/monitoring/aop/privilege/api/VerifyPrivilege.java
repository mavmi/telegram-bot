package mavmi.telegram_bot.monitoring.aop.privilege.api;

import mavmi.telegram_bot.common.privileges.api.PRIVILEGE;

public @interface VerifyPrivilege {
    PRIVILEGE value();
}
