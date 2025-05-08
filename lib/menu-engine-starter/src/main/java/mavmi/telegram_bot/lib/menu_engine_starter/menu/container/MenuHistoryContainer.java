package mavmi.telegram_bot.lib.menu_engine_starter.menu.container;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.menu_engine_starter.menu.container.exception.MenuHistoryContainerException;

import java.util.ArrayList;
import java.util.List;

public class MenuHistoryContainer {

    private final List<Menu> menuHistory = new ArrayList<>();

    public void add(Menu menu) {
        if (!menuHistory.isEmpty() && menuHistory.getLast().equals(menu)) {
            throw new MenuHistoryContainerException("User is already in meny " + menu.getName());
        }

        menuHistory.add(menu);
    }

    public Menu getLast() {
        return getLast(Menu.class);
    }

    public <T extends Menu> T getLast(Class<T> cls) {
        if (menuHistory.isEmpty()) {
            throw new MenuHistoryContainerException("Menu history is empty");
        }

        return (T) menuHistory.getLast();
    }

    public Menu getLastAndRemove() {
        return getLastAndRemove(Menu.class);
    }

    public <T extends Menu> T getLastAndRemove(Class<T> cls) {
        if (menuHistory.isEmpty()) {
            throw new MenuHistoryContainerException("Menu history is empty");
        }

        return (T) menuHistory.remove(menuHistory.size() - 1);
    }

    public int size() {
        return menuHistory.size();
    }

    public <T extends Menu> void deleteUntil(Class<T> cls, T until) {
        while (!menuHistory.isEmpty()) {
            if (!until.equals(getLast(cls))) {
                getLastAndRemove(cls);
            } else {
                break;
            }
        }
    }

    public void clear() {
        menuHistory.clear();
    }

    public void reset(Menu defaultMenu) {
        clear();
        add(defaultMenu);
    }
}
