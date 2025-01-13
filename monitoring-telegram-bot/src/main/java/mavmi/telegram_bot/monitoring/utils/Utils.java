package mavmi.telegram_bot.monitoring.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Utils {

    public static long parseTelegramId(String textMessage) {
        long errorValue = -1;

        try {
            long chatId = Long.parseLong(textMessage);
            if (chatId <= 0) {
                throw new Exception();
            }

            return chatId;
        } catch (Exception e) {
            return errorValue;
        }
    }
}
