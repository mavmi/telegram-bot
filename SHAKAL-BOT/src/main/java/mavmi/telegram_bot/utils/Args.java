package mavmi.telegram_bot.utils;

import org.springframework.boot.ApplicationArguments;

import java.util.List;

public class Args {
    private final static String TOKEN_FLAG = "token";
    private final static String LOG_FILE_FLAG = "log-file";
    private final static String ERR_MSG = "Invalid arguments!\n" +
            "Usage: java -jar SHAKAL-BOT.jar\n" +
            "--token=[TELEGRAM_BOT_API_TOKEN]\n" +
            "--log-file=[LOG_FILE_PATH]";

    private String token;
    private String logFile;

    public Args(ApplicationArguments args){
        if (args.getNonOptionArgs().size() != 0 ||
                !args.getOptionNames().contains(TOKEN_FLAG) ||
                !args.getOptionNames().contains(LOG_FILE_FLAG)){
            throw new ArgsException(ERR_MSG);
        }

        {
            List<String> values = args.getOptionValues(TOKEN_FLAG);
            if (values == null || values.size() != 1) throw new ArgsException(ERR_MSG);
            token = values.get(0);
        }

        {
            List<String> values = args.getOptionValues(LOG_FILE_FLAG);
            if (values == null || values.size() != 1) throw new ArgsException(ERR_MSG);
            logFile = values.get(0);
        }
    }

    public String getToken(){
        return token;
    }
    public String getLogFile(){
        return logFile;
    }
}
