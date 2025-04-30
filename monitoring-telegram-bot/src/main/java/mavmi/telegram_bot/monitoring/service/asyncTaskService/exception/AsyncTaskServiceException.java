package mavmi.telegram_bot.monitoring.service.asyncTaskService.exception;

public class AsyncTaskServiceException extends RuntimeException {

    public AsyncTaskServiceException(String msg) {
        super(msg);
    }

    public AsyncTaskServiceException(Exception e) {
        super(e);
    }
}
