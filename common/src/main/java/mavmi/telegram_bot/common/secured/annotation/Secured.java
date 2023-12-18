package mavmi.telegram_bot.common.secured.annotation;

import mavmi.telegram_bot.common.database.auth.BOT_NAME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Secured {
    BOT_NAME value();
}
