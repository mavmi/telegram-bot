package mavmi.telegram_bot.water_stuff.data.water.service;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.water_stuff.service.database.WaterStuffDatabaseService;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class WaterDataService {

    private final WaterStuffDatabaseService databaseService;

    @Nullable
    public WaterStuffDto get(Long userId, String name) {
        return databaseService.findByUserIdAndGroupName(userId, name);
    }

    public List<WaterStuffDto> getAll(Long userId) {
        return databaseService.findByUserId(userId);
    }

    public void put(WaterStuffDto dto) {
        if (databaseService.findByUserIdAndGroupName(dto.getUserId(), dto.getName()) != null) {
            databaseService.updateByUserIdAndGroupName(dto);
        } else {
            databaseService.save(dto);
        }
    }

    public void remove(Long userId, String name) {
        databaseService.removeByUserIdAndGroupName(userId, name);
    }

    public int size(Long userId) {
        return databaseService.findByUserId(userId).size();
    }

    public List<Long> getUsersIdx() {
        Set<Long> set = new HashSet<>();

        for (WaterStuffDto dto : databaseService.findAll()) {
            set.add(dto.getUserId());
        }

        return new ArrayList<>(set);
    }
}
