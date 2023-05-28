package mavmi.telegram_bot.water_stuff_bot.water;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Calen implements Water {
    private static final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static final String[] month = new String[]{
            "января",
            "февраля",
            "марта",
            "апреля",
            "мая",
            "июня",
            "июля",
            "августа",
            "сентября",
            "октября",
            "ноября",
            "декабря"
    };
    private static final String[] dayOfWeek = new String[]{
            "вс",
            "пн",
            "вт",
            "ср",
            "чт",
            "пт",
            "сб"
    };
    private Calendar date;

    public Calen(Calendar date){
        setDate(date);
    }
    public Calen(String date){
        setDate(date);
    }

    public void setDate(Calendar date){
        this.date = date;
        midnight(date);
    }
    public void setDate(String dateStr){
        if (dateStr.equals("null")) {
            date = null;
            return;
        }

        if (date == null) date = new GregorianCalendar();
        try {
            date.setTime(dateFormat.parse(dateStr));
            midnight(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public Calendar getDate(){
        return date;
    }

    public long daysDiff(){
        Calendar today = GregorianCalendar.getInstance();
        midnight(today);
        return (today.getTimeInMillis() - date.getTimeInMillis()) / 1000 / 60 / 60 / 24;
    }

    @Override
    public String toInfoString(){
        return toInfoString(true);
    }

    @Override
    public String toInfoString(boolean markdown) {
        if (date == null) return "null";

        long days = daysDiff();
        long mod = days % 10;
        StringBuilder builder = new StringBuilder();
        builder.append(date.get(GregorianCalendar.DAY_OF_MONTH))
                .append(" ")
                .append(month[date.get(GregorianCalendar.MONTH)])
                .append(" ")
                .append(date.get(GregorianCalendar.YEAR))
                .append(",")
                .append(" ")
                .append(dayOfWeek[date.get(GregorianCalendar.DAY_OF_WEEK) - 1])
                .append(" ")
                .append("(")
                .append(days)
                .append(" ");

        if ((days >= 10 && days <= 19) || (mod == 0 || mod >= 5)){
            builder.append("дней");
        } else if (mod == 1){
            builder.append("день");
        } else if (mod >= 2) {
            builder.append("дня");
        }

        return builder.append(")").toString();
    }

    @Override
    public String toFileString(){
        if (date == null) return "null";
        return dateFormat.format(date.getTime());
    }

    private void midnight(Calendar calendar){
        calendar.set(GregorianCalendar.HOUR, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);
    }

}
