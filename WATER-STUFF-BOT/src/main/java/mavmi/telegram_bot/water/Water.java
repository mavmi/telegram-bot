package mavmi.telegram_bot.water;

public interface Water {
    String toInfoString();
    String toInfoString(boolean markdown);
    String toFileString();
}
