package mavmi.telegram_bot.common.httpServlet.exception;

public class HttpServletException extends RuntimeException {

    public HttpServletException(Exception e) {
        super(e);
    }

    public HttpServletException(String msg) {
        super(msg);
    }
}
