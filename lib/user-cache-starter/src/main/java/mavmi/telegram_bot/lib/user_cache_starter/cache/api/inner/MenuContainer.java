package mavmi.telegram_bot.lib.user_cache_starter.cache.api.inner;

import mavmi.telegram_bot.lib.dto.service.menu.Menu;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Part of {@link DataCache}
 * Stores user's menu level history
 */
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
}
