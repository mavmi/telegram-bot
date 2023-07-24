package mavmi.telegram_bot.common.logger;

import com.pengrad.telegrambot.model.Message;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

public class Logger {
    private final static DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    private final static String logPrefix = "LOGGER";
    private final static String errPrefix = "ERROR";
    private static Logger logger = null;
    private JdbcTemplate jdbcTemplate = null;
    private FileWriter writer = null;

    private Logger(){

    }

    public static void init(String logFile){
        logger = new Logger();
        logger.setLogFile(logFile);
    }
    public static void init(String logFile, DataSource dataSource){
        logger = new Logger();
        logger.setLogFile(logFile)
                .setDataSource(dataSource);
    }
    public static Logger getInstance(){
        return logger;
    }

    public Logger setLogFile(String logFilePath){
        try {
            writer = new FileWriter(logFilePath, true);
        } catch (IOException e){
            writer = null;
            System.err.println(e.getMessage());
        }
        return this;
    }
    public Logger setDataSource(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
        return this;
    }

    public synchronized void log(Message message){
        if (jdbcTemplate == null) return;

        com.pengrad.telegrambot.model.User user = message.from();
        long val = (long)message.date() * 1000L;
        java.sql.Date date = new java.sql.Date(val);
        java.sql.Time time = new java.sql.Time(val);

        jdbcTemplate.update(
                "insert into \"user\" values(?, ?, ?, ?, ?);",
                user.id(),
                message.chat().id(),
                user.username(),
                user.firstName(),
                user.lastName()
        );

        jdbcTemplate.update(
                "insert into request values(?, ?, ?, ?);",
                user.id(),
                message.text(),
                date,
                time
        );
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
