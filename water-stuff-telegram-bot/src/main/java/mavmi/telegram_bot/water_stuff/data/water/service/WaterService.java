package mavmi.telegram_bot.water_stuff.data.water.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.lib.database_starter.repository.WaterRepository;
import mavmi.telegram_bot.water_stuff.data.exception.DataException;
import mavmi.telegram_bot.water_stuff.data.water.inner.WaterInfoContainer;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class WaterService {

    private final WaterRepository repository;

    public WaterModel get(Long userId, String name) {
        Optional<WaterModel> optional = repository.findByUserId(userId, name);

        return optional.orElse(null);
    }

    public List<WaterModel> getAll(Long userId) {
        return repository.findAll();
    }

    public boolean put(Long userId, WaterInfo waterInfo) {
        WaterInfoContainer waterInfoContainer = usersWaterDataMap.get(userId);

        if (waterInfoContainer == null) {
            waterInfoContainer = new WaterInfoContainer(userId);
            usersWaterDataMap.put(userId, waterInfoContainer);
        }

        if (waterInfoContainer.put(waterInfo)) {
            saveToFile();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(Long userId, String name) {
        WaterInfoContainer waterInfoContainer = usersWaterDataMap.get(userId);

        if (waterInfoContainer != null && waterInfoContainer.remove(name)) {
            saveToFile();
            return true;
        } else {
            return false;
        }
    }

    public int size(Long userId) {
        WaterInfoContainer waterInfoContainer = usersWaterDataMap.get(userId);

        if (waterInfoContainer == null) {
            return 0;
        } else {
            return waterInfoContainer.size();
        }
    }

    public List<Long> getUsersIdx() {
        List<Long> list = new ArrayList<>();

        for (Map.Entry<Long, WaterInfoContainer> entry : usersWaterDataMap.entrySet()) {
            list.add(entry.getKey());
        }

        return list;
    }

    public void loadFromFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            usersWaterDataMap = objectMapper.readValue(dataFile, new TypeReference<HashMap<Long, WaterInfoContainer>>() {});
        } catch (IOException e) {
            if (dataFile.exists()) {
                throw new DataException(e);
            }
        }
    }
}
