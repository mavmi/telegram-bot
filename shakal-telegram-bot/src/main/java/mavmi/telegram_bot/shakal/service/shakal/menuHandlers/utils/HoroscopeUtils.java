package mavmi.telegram_bot.shakal.service.shakal.menuHandlers.utils;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public class HoroscopeUtils {

    private static final Map<String, String> HOROSCOPE_SIGNS = new HashMap<>() {{
        put("Овен", "aries");
        put("Лев", "leo");
        put("Стрелец", "sagittarius");
        put("Телец", "taurus");
        put("Дева", "virgo");
        put("Козерог", "capricorn");
        put("Близнецы", "gemini");
        put("Весы", "libra");
        put("Водолей", "aquarius");
        put("Рак", "cancer");
        put("Скорпион", "scorpio");
        put("Рыбы", "pisces");
    }};

    public String getSign(String sign) {
        return HOROSCOPE_SIGNS.get(sign);
    }
}
