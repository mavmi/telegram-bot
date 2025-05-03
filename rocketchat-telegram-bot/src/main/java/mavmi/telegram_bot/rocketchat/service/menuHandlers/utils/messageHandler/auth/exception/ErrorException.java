package mavmi.telegram_bot.rocketchat.service.menuHandlers.utils.messageHandler.auth.exception;

public class ErrorException extends RuntimeException {

    public ErrorException(String msg) {
        super(msg);
    }
}
