package mavmi.telegram_bot.lib.menu_engine_starter.menu.container.exception;

public class MenuHistoryContainerException extends RuntimeException {

    public MenuHistoryContainerException(String msg) {
        super(msg);
    }

    public MenuHistoryContainerException(Exception e) {
        super(e);
    }
}
