package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import java.text.SimpleDateFormat;

public abstract class CommonUtils {
    public static final SimpleDateFormat INPUT_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    public static final SimpleDateFormat OUTPUT_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");
}
