package mavmi.telegram_bot.common.secured.annotation;

import mavmi.telegram_bot.common.database.auth.BotNames;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface Secured {
    BotNames value();
}
