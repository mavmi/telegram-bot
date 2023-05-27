package mavmi.telegram_bot.shakal_bot.args;

public class Args {
    private final static String TOKEN_FLAG = "--token";
    private final static String LOG_FILE_FLAG = "--log-file";
    private final static String ERR_MSG = "Invalid arguments!\n" +
            "Usage: java -jar SHAKAL-BOT.jar\n" +
            "\t--token=[TELEGRAM_BOT_API_TOKEN]\n" +
            "\t--log-file=[LOG_FILE_PATH]";

    private String token;
    private String logFile;

    public Args(String[] args){
        if (args.length != 2) throw new ArgsException(ERR_MSG);

        for (String str : args){
            String[] splitted = str.split("=");
            if (splitted.length != 2) throw new ArgsException(ERR_MSG);

            String flag = splitted[0];
            String value = splitted[1];

            switch (flag){
                case (TOKEN_FLAG) -> token = value;
                case (LOG_FILE_FLAG) -> logFile = value;
                default -> throw new ArgsException(ERR_MSG);
            }
        }
    }

    public String getToken(){
        return token;
    }
    public String getLogFile(){
        return logFile;
    }

}
