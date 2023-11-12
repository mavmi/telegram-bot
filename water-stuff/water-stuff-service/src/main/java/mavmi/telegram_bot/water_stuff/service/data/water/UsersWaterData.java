package mavmi.telegram_bot.water_stuff.service.data.water;

import mavmi.telegram_bot.water_stuff.service.data.DataException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
public class UsersWaterData {
    private final File dataFile;
    private final Map<Long, WaterInfoContainer> usersWaterDataMap;

    public UsersWaterData(@Value("${service.data-file}") String workingFilePath) {
        this.dataFile = new File(workingFilePath);
        this.usersWaterDataMap = new HashMap<>();
        loadFromFile();
    }

    @Nullable
    public WaterInfo get(Long userId, String name) {
        WaterInfoContainer waterInfoContainer = usersWaterDataMap.get(userId);

        return (waterInfoContainer == null) ?
                null :
                waterInfoContainer.get(name);
    }

    @Nullable
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
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile))) {
            usersWaterDataMap.clear();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                WaterInfo waterInfo = WaterInfo.fromFileString(line);
                put(waterInfo.getUserId(), waterInfo);
            }
        } catch (IOException | NumberFormatException e) {
            if (dataFile.exists()) {
                throw new DataException(e);
            }
        }
    }

    public void saveToFile() {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(dataFile))) {
            StringBuilder stringBuilder = new StringBuilder();

            for (Map.Entry<Long, WaterInfoContainer> entry : usersWaterDataMap.entrySet()) {
                for (WaterInfo waterInfo : entry.getValue().asList()) {
                    stringBuilder.append(waterInfo.toFileString()).append("\n");
                }
            }

            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new DataException(e);
        }
    }
}
