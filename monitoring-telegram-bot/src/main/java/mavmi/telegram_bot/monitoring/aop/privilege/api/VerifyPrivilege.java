package mavmi.telegram_bot.monitoring.aop.privilege.api;

import mavmi.telegram_bot.lib.database_starter.api.PRIVILEGE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VerifyPrivilege {
    PRIVILEGE value();
}
