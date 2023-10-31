package mavmi.telegram_bot.water_stuff_bot.data;

public class DataException extends RuntimeException {
    public DataException(String msg) {
        super(msg);
    }

    public DataException(Exception e) {
        super(e);
    }
}
