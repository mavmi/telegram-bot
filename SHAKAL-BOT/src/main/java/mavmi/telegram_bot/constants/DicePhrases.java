package mavmi.telegram_bot.constants;

public class DicePhrases {
    private final static String[] win = new String[]{
            "Умничка",
            "Щиииит ладно",
            "Го еще одну",
            "Ок",
            "Молодец! Продолжай в том же духе!"
    };

    private final static String[] lose = new String[]{
            "Легкая",
            "ez",
            "Слабый",
            "Лох",
            "Лох слабый"
    };

    public static String getRandomWinPhrase(){
        return win[(int)(Math.random() * win.length)];
    }
    public static String getRandomLosePhrase(){
        return lose[(int)(Math.random() * lose.length)];
    }
}
