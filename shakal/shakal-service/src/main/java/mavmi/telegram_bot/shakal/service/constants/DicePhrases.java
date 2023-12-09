package mavmi.telegram_bot.shakal.service.constants;

public class DicePhrases {

    private static final String[] WIN = new String[]{
            "Умничка",
            "Щиииит ладно",
            "Го еще одну",
            "Ок",
            "Молодец! Продолжай в том же духе!"
    };

    private static final String[] DRAW = new String[]{
            "штош..."
    };

    private static final String[] LOSE = new String[]{
            "Легкая",
            "ez",
            "Слабый",
            "Лох",
            "Лох слабый"
    };

    public static String getRandomWinPhrase(){
        return WIN[(int)(Math.random() * WIN.length)];
    }

    public static String getRandomDrawPhrase() {
        return DRAW[(int)(Math.random() * DRAW.length)];
    }

    public static String getRandomLosePhrase(){
        return LOSE[(int)(Math.random() * LOSE.length)];
    }
}
