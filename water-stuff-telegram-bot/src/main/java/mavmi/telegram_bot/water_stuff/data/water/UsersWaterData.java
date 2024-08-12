package mavmi.telegram_bot.water_stuff.data.water;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import mavmi.telegram_bot.water_stuff.data.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UsersWaterData {
    private final File dataFile;

    private Map<Long, WaterInfoContainer> usersWaterDataMap;

    public UsersWaterData(@Value("${service.data-file}") String workingFilePath) {
        this.dataFile = new File(workingFilePath);
        this.usersWaterDataMap = new HashMap<>();
        loadFromFile();
    }

    public WaterInfo get(Long userId, String name) {
        WaterInfoContainer waterInfoContainer = usersWaterDataMap.get(userId);

        return (waterInfoContainer == null) ?
                null :
                waterInfoContainer.get(name);
    }

    public List<WaterInfo> getAll(Long userId) {
        WaterInfoContainer waterInfoContainer = usersWaterDataMap.get(userId);

        return (waterInfoContainer == null) ? null : waterInfoContainer.asList();
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

    public void saveToFile() {
        ObjectMapper objectMapper = new ObjectMapper();

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile))) {
            bufferedWriter.write(objectMapper.writeValueAsString(usersWaterDataMap));
        } catch (IOException e) {
            throw new DataException(e);
        }
    }
}
