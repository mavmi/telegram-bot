package mavmi.telegram_bot.rocketchat.service.serviceComponents.serviceModule.auth.messageHandler.exception;

public class ErrorException extends RuntimeException {

    public ErrorException(String msg) {
        super(msg);
    }
}
