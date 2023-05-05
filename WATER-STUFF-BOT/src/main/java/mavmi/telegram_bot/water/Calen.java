package mavmi.telegram_bot.water;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Calen {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private Calendar date;

    public Calen(Calendar date){
        setDate(date);
    }
    public Calen(String date){
        setDate(date);
    }

    public void setDate(Calendar date){
        this.date = date;
    }
    public void setDate(String dateStr){
        try {
            date.setTime(dateFormat.parse(dateStr));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public Calendar getDate(){
        return date;
    }

    @Override
    public String toString() {
        return dateFormat.format(date.getTime());
    }
}
