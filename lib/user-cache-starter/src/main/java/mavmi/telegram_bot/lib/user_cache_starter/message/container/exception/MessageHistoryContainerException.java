package mavmi.telegram_bot.lib.user_cache_starter.message.container.exception;

public class MessageHistoryContainerException extends RuntimeException {

    public MessageHistoryContainerException(String msg) {
        super(msg);
    }

    public MessageHistoryContainerException(Exception e) {
        super(e);
    }
}
