package mavmi.telegram_bot.water;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WaterContainer {
    private final List<WaterInfo> waterInfoList;
    private final File workingFile;

    public WaterContainer(String workingFilePath){
        waterInfoList = new ArrayList<>();
        workingFile = new File(workingFilePath);
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

    public void toFile(){
        try (FileWriter writer = new FileWriter(workingFile, false)) {
            for (WaterInfo waterInfo : waterInfoList){
                writer.write(waterInfo.toString());
            }
            writer.flush();
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }
    public void fromFile(){
        waterInfoList.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(workingFile))){
            String name, water, fertilize;
            while (true){
                name = reader.readLine();
                water = reader.readLine();
                fertilize = reader.readLine();
                if (name == null || water == null || fertilize == null) break;
                WaterInfo waterInfo = new WaterInfo();
                waterInfo.setName(name);
                waterInfo.setWater(new Calen(water));
                waterInfo.setFertilize(new Calen(fertilize));
                waterInfoList.add(waterInfo);
            }
        } catch (IOException e){
            System.err.println(e.getMessage());
        }
    }

}
