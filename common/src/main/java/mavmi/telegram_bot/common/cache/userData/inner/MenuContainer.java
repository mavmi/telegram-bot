package mavmi.telegram_bot.common.cache.userData.inner;

import mavmi.telegram_bot.common.service.menu.Menu;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MenuContainer {

    private final List<Menu> menuList = new ArrayList<>();

    @Nullable
    public Menu getLast() {
        int size = menuList.size();
        if (size == 0) {
            return null;
        }

        return menuList.get(size - 1);
    }

    public void removeLast() {
        int size = menuList.size();
        if (size != 0) {
            menuList.remove(size - 1);
        }
    }

    public void add(Menu menu) {
        menuList.add(menu);
    }

    public void clear(Menu defaultMenu) {
        menuList.clear();
        menuList.add(defaultMenu);
    }

    public void clear() {
        menuList.clear();
    }
}
