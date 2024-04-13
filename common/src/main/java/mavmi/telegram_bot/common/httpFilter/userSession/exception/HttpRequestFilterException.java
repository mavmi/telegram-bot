package mavmi.telegram_bot.common.httpFilter.userSession.exception;

public class HttpRequestFilterException extends RuntimeException {

    public HttpRequestFilterException(String msg) {
        super(msg);
    }

    public HttpRequestFilterException(Exception e) {
        super(e);
    }
}
