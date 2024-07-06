package mavmi.telegram_bot.monitoring.asyncTaskService;

public class AsyncTaskServiceException extends RuntimeException {

    public AsyncTaskServiceException(String msg) {
        super(msg);
    }

    public AsyncTaskServiceException(Exception e) {
        super(e);
    }
}
