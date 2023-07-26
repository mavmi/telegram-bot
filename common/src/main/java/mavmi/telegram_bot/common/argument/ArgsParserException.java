package mavmi.telegram_bot.common.argument;

public class ArgsParserException extends RuntimeException{
    public ArgsParserException(String msg){
        super(msg);
    }
    public ArgsParserException(Throwable e){
        super(e);
    }
}
