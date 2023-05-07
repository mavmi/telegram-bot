package mavmi.telegram_bot.telegram_bot;

import com.pengrad.telegrambot.model.Message;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Logger {
    private final static DateFormat dateTimeFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");

    private FileWriter writer;
    private JdbcTemplate jdbcTemplate;

    public Logger(){

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
    public void log(Message message){
        com.pengrad.telegrambot.model.User user = message.from();
        long val = (long)message.date() * 1000L;
        java.sql.Date date = new java.sql.Date(val);
        java.sql.Time time = new java.sql.Time(val);

        jdbcTemplate.update(
                "insert into \"user\" values(?, ?, ?, ?);",
                user.id(),
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

    private String getDate(){
        return dateTimeFormat.format(GregorianCalendar.getInstance().getTime());
    }

}
