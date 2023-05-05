package mavmi.telegram_bot.water;

public class WaterInfo {
    private String name;
    private Calen water;
    private Calen fertilize;

    public WaterInfo(){

    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }

    public void setWater(Calen water){
        this.water = water;
    }
    public Calen getWater(){
        return water;
    }

    public void setFertilize(Calen fertilize){
        this.fertilize = fertilize;
    }
    public Calen getFertilize(){
        return fertilize;
    }

    @Override
    public String toString() {
        return name + "\n" +
                water.toString() + "\n" +
                fertilize.toString() + "\n";
    }
}
