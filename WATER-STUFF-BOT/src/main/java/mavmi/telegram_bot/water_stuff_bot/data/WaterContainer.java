package mavmi.telegram_bot.water_stuff_bot.data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class WaterContainer {
    private final File dataFile;
    private final Map<String, WaterInfo> dataMap = new HashMap<>();

    public WaterContainer(@Value("${bot.data-file}") String workingFilePath) {
        this.dataFile = new File(workingFilePath);
        loadFromFile();
    }

    public WaterInfo get(String name) {
        return dataMap.get(name);
    }

    public void put(WaterInfo waterInfo) {
        dataMap.put(waterInfo.getName(), waterInfo);
        saveToFile();
    }

    public void remove(String name) {
        dataMap.remove(name);
        saveToFile();
    }

    public Set<Map.Entry<String, WaterInfo>> entrySet() {
        return dataMap.entrySet();
    }

    public int size() {
        return dataMap.size();
    }

    public void loadFromFile() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(dataFile))) {
            dataMap.clear();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }

                String[] splittedLine = line.split(";");
                if (splittedLine.length != 4) {
                    throw new DataException("Invalid data line format");
                }

                WaterInfo waterInfo = new WaterInfo();
                waterInfo.setName(splittedLine[0]);
                waterInfo.setDiff(Integer.parseInt(splittedLine[1]));
                waterInfo.setWater(splittedLine[2]);
                waterInfo.setFertilize(splittedLine[3]);

                dataMap.put(waterInfo.getName(), waterInfo);
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

            for (Map.Entry<String, WaterInfo> entry : dataMap.entrySet()) {
                WaterInfo waterInfo = entry.getValue();
                stringBuilder.append(waterInfo.getName())
                        .append(";")
                        .append(waterInfo.getDiff())
                        .append(";")
                        .append(waterInfo.getWaterAsString())
                        .append(";")
                        .append(waterInfo.getFertilizeAsString())
                        .append("\n");
            }

            bufferedWriter.write(stringBuilder.toString());
        } catch (IOException e) {
            throw new DataException(e);
        }
    }
}
