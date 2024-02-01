package mavmi.telegram_bot.common.httpFilter.exception;

import mavmi.telegram_bot.common.httpFilter.HttpRequestFilter;

/**
 * Исключение для класса {@link HttpRequestFilter}
 */
public class HttpRequestFilterException extends RuntimeException {

    public HttpRequestFilterException(String msg) {
        super(msg);
    }

    public HttpRequestFilterException(Exception e) {
        super(e);
    }
}
