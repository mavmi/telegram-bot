package mavmi.telegram_bot.async_task_manager.service;

public class AsyncTaskServiceException extends RuntimeException {

    public AsyncTaskServiceException(String msg) {
        super(msg);
    }

    public AsyncTaskServiceException(Exception e) {
        super(e);
    }
}
