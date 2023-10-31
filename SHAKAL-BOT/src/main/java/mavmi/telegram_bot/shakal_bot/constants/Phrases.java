package mavmi.telegram_bot.shakal_bot.constants;

import java.util.HashMap;
import java.util.Map;

public abstract class Phrases {
    public final static String DICE_THROW_MSG = "\uD83C\uDFB2";
    public final static String DICE_QUIT_MSG = "дать заднюю (выйти)";
    public final static String DICE_OK_MSG = "Ладно";
    public final static String DICE_ERROR_MSG = "Каво?";

    public final static String HOROSCOPE_QUES_MSG = "Ты кто по гороскопу в принципе?";
    public final static String HOROSCOPE_ERROR_MSG = "а?";
    public final static Map<String, String> HOROSCOPE_SIGNS = new HashMap<>();
    static {
        HOROSCOPE_SIGNS.put("Овен", "aries");
        HOROSCOPE_SIGNS.put("Лев", "leo");
        HOROSCOPE_SIGNS.put("Стрелец", "sagittarius");
        HOROSCOPE_SIGNS.put("Телец", "taurus");
        HOROSCOPE_SIGNS.put("Дева", "virgo");
        HOROSCOPE_SIGNS.put("Козерог", "capricorn");
        HOROSCOPE_SIGNS.put("Близнецы", "gemini");
        HOROSCOPE_SIGNS.put("Весы", "libra");
        HOROSCOPE_SIGNS.put("Водолей", "aquarius");
        HOROSCOPE_SIGNS.put("Рак", "cancer");
        HOROSCOPE_SIGNS.put("Скорпион", "scorpio");
        HOROSCOPE_SIGNS.put("Рыбы", "pisces");
    }

    public final static String GREETINGS_MSG = "Здравствуйте.";
    public final static String APOLOCHEESE_MSG = "Для кого оформляем, брат?";
    public final static String EXCEPTION_MSG = "Сорян, братишка. Что-то не так пошло. Попробуй по новой?";
    public final static String INVALID_COMMAND_MSG = "Я не вдупляю";
}
