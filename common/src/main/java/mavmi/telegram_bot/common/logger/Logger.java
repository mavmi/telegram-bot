package mavmi.telegram_bot.common.logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

@Component
public class Logger {
    private final static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private final static String logPrefix = "LOGGER";
    private final static String errPrefix = "ERROR";

    private FileWriter writer;

    public Logger(@Value("${bot.logfile}") String logFile){
        try {
            writer = new FileWriter(logFile, true);
        } catch (IOException e){
            writer = null;
            System.err.println(e.getMessage());
        }
    }

    public synchronized void log(String msg){
        write(logPrefix, msg);
    }
    public synchronized void err(String msg){
        write(errPrefix, msg);
    }

    private void write(String prefix, String msg){
        final String msgWithDate = "[" + prefix + "]" + "\t" + getDate() + ": " + msg;

        if (writer != null) {
            try {
                writer.append(msgWithDate);
                writer.append("\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        System.out.println(msgWithDate);
    }

    private String getDate(){
        return dateFormat.format(GregorianCalendar.getInstance().getTime());
    }

}
