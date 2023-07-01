package mavmi.telegram_bot.crv_bot.args;

public class Args {
    private final static String BOT_TOKEN_FLAG = "--bot-token";
    private final static String LOG_FILE_FLAG = "--log-file";
    private final static String ERR_MSG = "Invalid arguments!\n" +
            "Usage: java -jar crv-bot.jar\n" +
            "\t" + BOT_TOKEN_FLAG + "=[TELEGRAM_BOT_TOKEN]\n" +
            "\t" + LOG_FILE_FLAG + "=[BOT_LOG_FILE]\n";

    private String botToken;
    private String logFile;

    public Args(String[] args){
        if (args.length != 2) throw new ArgsException(ERR_MSG);

        for (String str : args){
            String[] splitted = str.split("=");
            if (splitted.length != 2) throw new ArgsException(ERR_MSG);

            String flag = splitted[0];
            String value = splitted[1];

            switch (flag){
                case (BOT_TOKEN_FLAG) -> botToken = value;
                case (LOG_FILE_FLAG) -> logFile = value;
                default -> throw new ArgsException(ERR_MSG);
            }
        }
    }

    public String getBotToken(){
        return botToken;
    }
    public String getLogFile(){
        return logFile;
    }
}
