package mavmi.telegram_bot.water;

import mavmi.telegram_bot.telegram_bot.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WaterContainer {
    private final List<WaterInfo> waterInfoList;
    private final File workingFile;
    private final Logger logger;

    public WaterContainer(String workingFilePath){
        waterInfoList = new ArrayList<>();
        workingFile = new File(workingFilePath);
        logger = Logger.getInstance();
        fromFile();
    }

    public void add(WaterInfo waterInfo){
        waterInfoList.add(waterInfo);
    }
    public WaterInfo get(int pos){
        return waterInfoList.get(pos);
    }
    public int size(){
        return waterInfoList.size();
    }
    public void remove(int i){
        waterInfoList.remove(i);
    }

    public void toFile(){
        try (FileWriter writer = new FileWriter(workingFile, false)) {
            for (WaterInfo waterInfo : waterInfoList){
                writer.append(waterInfo.toFileString());
            }
            writer.flush();
        } catch (IOException e){
            logger.err(e.getMessage());
        }
    }
    public void fromFile(){
        waterInfoList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(workingFile))){
            String name, diff, water, fertilize;
            while (true){
                name = reader.readLine();
                diff = reader.readLine();
                water = reader.readLine();
                fertilize = reader.readLine();
                if (name == null || diff == null || water == null || fertilize == null) break;
                WaterInfo waterInfo = new WaterInfo();
                waterInfo.setName(name)
                        .setDiff(Integer.parseInt(diff))
                        .setWater(new Calen(water))
                        .setFertilize(new Calen(fertilize));
                waterInfoList.add(waterInfo);
            }
        } catch (IOException | NumberFormatException e){
            logger.err(e.getMessage());
        }
    }

}
