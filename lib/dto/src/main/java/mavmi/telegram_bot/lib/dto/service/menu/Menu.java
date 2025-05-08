package mavmi.telegram_bot.lib.dto.service.menu;

/**
 * Telegram bot's menu items
 */
public interface Menu {
    String getName();
    Menu getParent();
    Menu findByName(String name);
}
