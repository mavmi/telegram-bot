package mavmi.telegram_bot.common.arguments.exception;

public class ArgumentsParserException extends RuntimeException{
    public ArgumentsParserException(String msg){
        super(msg);
    }
    public ArgumentsParserException(Throwable e){
        super(e);
    }
}
