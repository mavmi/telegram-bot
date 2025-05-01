package mavmi.telegram_bot.lib.metric_starter.mteric.api;

import mavmi.telegram_bot.lib.database_starter.api.BOT_NAME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Is used to collect metrics about bot usage
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Metric {

    BOT_NAME value();
}
