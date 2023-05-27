package mavmi.telegram_bot.water_stuff_bot.water;

public interface Water {
    String toInfoString();
    String toInfoString(boolean markdown);
    String toFileString();
}
