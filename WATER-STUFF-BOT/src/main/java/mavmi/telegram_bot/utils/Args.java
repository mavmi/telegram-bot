package mavmi.telegram_bot.utils;

import org.springframework.boot.ApplicationArguments;

import java.util.List;

public class Args {
    private final static String BOT_TOKEN_FLAG = "token";
    private final static String LOG_FILE_FLAG = "log-file";
    private final static String WORKING_FILE_FLAG = "file";
    private final static String USER_FLAG = "username";
    private final static String ERR_MSG = "Invalid arguments!\n" +
                                            "Usage: java -jar water-stuff-bot.jar\n" +
                                            "\t--" + BOT_TOKEN_FLAG + "=[TELEGRAM_BOT_TOKEN]\n" +
                                            "\t--" + LOG_FILE_FLAG + "=[BOT_LOG_FILE]\n" +
                                            "\t--" + WORKING_FILE_FLAG + "=[BOT_WORKING_FILE]\n" +
                                            "\t--" + USER_FLAG + "=[AVAILABLE_USERNAME]";

    private String botToken;
    private String logFile;
    private String workingFile;
    private String availableUser;

    public Args(ApplicationArguments args){
        if (args.getNonOptionArgs().size() != 0 || !args.getOptionNames().contains(BOT_TOKEN_FLAG) ||
                !args.getOptionNames().contains(LOG_FILE_FLAG) || !args.getOptionNames().contains(WORKING_FILE_FLAG) ||
                !args.getOptionNames().contains(USER_FLAG)){
            throw new ArgsException(ERR_MSG);
        }

        {
            List<String> values = args.getOptionValues(BOT_TOKEN_FLAG);
            if (values == null || values.size() != 1) throw new ArgsException(ERR_MSG);
            botToken = values.get(0);
        }
        {
            List<String> values = args.getOptionValues(LOG_FILE_FLAG);
            if (values == null || values.size() != 1) throw new ArgsException(ERR_MSG);
            logFile = values.get(0);
        }
        {
            List<String> values = args.getOptionValues(WORKING_FILE_FLAG);
            if (values == null || values.size() != 1) throw new ArgsException(ERR_MSG);
            workingFile = values.get(0);
        }
        {
            List<String> values = args.getOptionValues(USER_FLAG);
            if (values == null || values.size() != 1) throw new ArgsException(ERR_MSG);
            availableUser = values.get(0);
        }
    }

    public String getBotToken() {
        return botToken;
    }
    public String getLogFile(){
        return logFile;
    }
    public String getWorkingFile() {
        return workingFile;
    }
    public String getAvailableUser() {
        return availableUser;
    }
}
