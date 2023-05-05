package mavmi.telegram_bot.telegram_bot;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Logger {
    private final static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private FileWriter writer;

    public Logger(String logFilePath){
        try {
            writer = new FileWriter(logFilePath, true);
        } catch (IOException e){
            writer = null;
            System.err.println(e.getMessage());
        }
    }

    public void log(String msg){
        final String msgWithDate = getDate() + ": " + msg;

        if (writer != null) {
            try {
                writer.append(msgWithDate);
                writer.append("\n");
                writer.flush();
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }

        System.err.print("LOGGER\t");
        System.err.println(msgWithDate);
    }

    private String getDate(){
        return dateFormat.format(GregorianCalendar.getInstance().getTime());
    }
}
