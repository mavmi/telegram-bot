package mavmi.telegram_bot.common.aop.metric.api;

import mavmi.telegram_bot.common.database.auth.BOT_NAME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {

    BOT_NAME value();
}
