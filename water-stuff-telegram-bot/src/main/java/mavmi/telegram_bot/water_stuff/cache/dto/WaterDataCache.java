package mavmi.telegram_bot.water_stuff.cache.dto;

import lombok.Getter;
import lombok.Setter;
import mavmi.telegram_bot.lib.user_cache_starter.cache.api.DataCache;
import mavmi.telegram_bot.water_stuff.cache.dto.inner.GroupsCache;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import mavmi.telegram_bot.water_stuff.service.waterStuff.menu.WaterStuffServiceMenu;

import java.util.List;

/**
 * {@inheritDoc}
 */
@Getter
@Setter
public class WaterDataCache extends DataCache {

    private String selectedGroup;
    private GroupsCache groupsCache = new GroupsCache();

    public WaterDataCache(Long userId,
                          WaterStuffServiceMenu menu) {
        super(userId, menu);
    }

    public void addGroup(WaterStuffDto dto) {
        groupsCache.add(dto);
    }

    public void removeGroup(String name) {
        groupsCache.remove(name);
    }

    public WaterStuffDto getGroup(String name) {
        return groupsCache.get(name);
    }

    public List<WaterStuffDto> getAllGroups() {
        return groupsCache.getAll();
    }

    public int getGroupsSize() {
        return groupsCache.size();
    }
}
