package mavmi.telegram_bot.water_stuff.service.waterStuff.menuHandlers.utils;

import lombok.SneakyThrows;
import mavmi.telegram_bot.lib.dto.service.common.InlineKeyboardJson;
import mavmi.telegram_bot.lib.dto.service.common.inlineKeyboard.InlineKeyboardButtonJson;
import mavmi.telegram_bot.lib.dto.service.common.inlineKeyboard.InlineKeyboardRowJson;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CalendarUtils {

    public static final DateFormat MM_YY_DATE_FORMAT = new SimpleDateFormat("MM-yyyy") {{ setLenient(false); }};
    public static final DateFormat DD_MM_YY_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy") {{ setLenient(false); }};
    private static final Map<Integer, String> MONTH_NAMES = new HashMap<>() {{
        put(1, "Январь");
        put(2, "Февраль");
        put(3, "Март");
        put(4, "Апрель");
        put(5, "Май");
        put(6, "Июнь");
        put(7, "Июль");
        put(8, "Август");
        put(9, "Сентябрь");
        put(10, "Октярь");
        put(11, "Ноябрь");
        put(12, "Декабрь");
    }};

    public InlineKeyboardJson getMonthKeyboard() {
        int month = GregorianCalendar.getInstance().get(GregorianCalendar.MONTH) + 1;
        int year = GregorianCalendar.getInstance().get(GregorianCalendar.YEAR);

        return getCalendar(month, year);
    }

    @SneakyThrows
    public InlineKeyboardJson getMonthKeyboard(String dateStr) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(MM_YY_DATE_FORMAT.parse(dateStr));
        int month = calendar.get(GregorianCalendar.MONTH) + 1;
        int year = calendar.get(GregorianCalendar.YEAR);

        return getCalendar(month, year);
    }

    public String getMonthYear() {
        Calendar calendar = new GregorianCalendar();
        return MONTH_NAMES.get(calendar.get(GregorianCalendar.MONTH) + 1) +
                ", " +
                calendar.get(GregorianCalendar.YEAR);
    }

    @Nullable
    @SneakyThrows
    public String getMonthYear(String dateStr) {
        Date date;

        if (isDayFormat(dateStr)) {
            date = DD_MM_YY_DATE_FORMAT.parse(dateStr);
        } else if (isMonthFormat(dateStr)) {
            date = MM_YY_DATE_FORMAT.parse(dateStr);
        } else {
            return null;
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        return MONTH_NAMES.get(calendar.get(GregorianCalendar.MONTH) + 1) +
                ", " +
                calendar.get(GregorianCalendar.YEAR);
    }

    public boolean isDayFormat(String str) {
        try {
            DD_MM_YY_DATE_FORMAT.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isMonthFormat(String str) {
        try {
            MM_YY_DATE_FORMAT.parse(str);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private InlineKeyboardJson getCalendar(int month, int year) {
        Calendar calendar = GregorianCalendar.getInstance();

        calendar.set(GregorianCalendar.MONTH, month - 1);
        calendar.set(GregorianCalendar.YEAR, year);
        calendar.set(GregorianCalendar.HOUR_OF_DAY, 0);
        calendar.set(GregorianCalendar.MINUTE, 0);
        calendar.set(GregorianCalendar.SECOND, 0);
        calendar.set(GregorianCalendar.MILLISECOND, 0);

        List<InlineKeyboardRowJson> allWeeks = new ArrayList<>();
        int maxDayOfMonth = calendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        int dayOfMonth = 1;

        while(dayOfMonth <= maxDayOfMonth) {
            List<InlineKeyboardButtonJson> week = Stream
                    .generate(InlineKeyboardButtonJson::new)
                    .limit(7)
                    .collect(Collectors.toList());

            for (int i = 0; i < 7; i++) {
                calendar.set(GregorianCalendar.DAY_OF_MONTH, dayOfMonth);
                int dayOfWeek = calendar.get(GregorianCalendar.DAY_OF_WEEK);
                dayOfWeek = (dayOfWeek == 1) ? 6 : dayOfWeek - 2;

                if (i == dayOfWeek && dayOfMonth <= maxDayOfMonth) {
                    week.set(i, new InlineKeyboardButtonJson(
                            String.valueOf(dayOfMonth),
                            DD_MM_YY_DATE_FORMAT.format(Date.from(calendar.toInstant()))
                    ));
                    dayOfMonth++;
                } else {
                    week.set(i, new InlineKeyboardButtonJson(" ", " "));
                }
            }

            allWeeks.add(new InlineKeyboardRowJson(week));
        }

        int curMonth = month - 1;
        int curYear = year;
        calendar.set(GregorianCalendar.DAY_OF_MONTH, 1);

        int nextMonth = (curMonth == 11) ? 0 : curMonth + 1;
        int nextYear = (curMonth == 11) ? curYear + 1 : curYear;
        calendar.set(GregorianCalendar.MONTH, nextMonth);
        calendar.set(GregorianCalendar.YEAR, nextYear);
        InlineKeyboardButtonJson nextMonthButton = new InlineKeyboardButtonJson(
                "==>",
                MM_YY_DATE_FORMAT.format(Date.from(calendar.toInstant()))
        );

        int prevMonth = (curMonth == 0) ? 11 : curMonth - 1;
        int prevYear = (curMonth == 0) ? curYear - 1 : curYear;
        calendar.set(GregorianCalendar.MONTH, prevMonth);
        calendar.set(GregorianCalendar.YEAR, prevYear);
        InlineKeyboardButtonJson prevMonthButton = new InlineKeyboardButtonJson(
                "<==",
                MM_YY_DATE_FORMAT.format(Date.from(calendar.toInstant()))
        );

        allWeeks.add(new InlineKeyboardRowJson(List.of(prevMonthButton, nextMonthButton)));
        return new InlineKeyboardJson(allWeeks);
    }
}
