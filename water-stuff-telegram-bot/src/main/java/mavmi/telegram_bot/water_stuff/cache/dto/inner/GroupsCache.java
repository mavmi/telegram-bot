package mavmi.telegram_bot.water_stuff.cache.dto.inner;

import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsCache {

    private final Map<String, WaterStuffDto> nameToGroup = new HashMap<>();

    public void add(WaterStuffDto dto) {
        nameToGroup.put(dto.getName(), dto);
    }

    public void remove(String name) {
        nameToGroup.remove(name);
    }

    @Nullable
    public WaterStuffDto get(String name) {
        return nameToGroup.get(name);
    }

    public List<WaterStuffDto> getAll() {
        return new ArrayList<>(nameToGroup.values());
    }

    public int size() {
        return nameToGroup.size();
    }
}
