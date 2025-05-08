package mavmi.telegram_bot.lib.menu_engine_starter.engine.exception;

public class MenuEngineException extends RuntimeException {

    public MenuEngineException(String msg) {
        super(msg);
    }

    public MenuEngineException(Exception e) {
        super(e);
    }
}
