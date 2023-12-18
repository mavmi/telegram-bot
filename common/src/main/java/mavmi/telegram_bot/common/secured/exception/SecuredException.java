package mavmi.telegram_bot.common.secured.exception;

public class SecuredException extends RuntimeException {

    public SecuredException(String msg) {
        super(msg);
    }

    public SecuredException(Exception e) {
        super(e);
    }
}
