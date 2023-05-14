package mavmi.telegram_bot.water;

public class WaterInfo {
    private String name;
    private Calen water;
    private Calen fertilize;

    public WaterInfo(){

    }

    public WaterInfo setName(String name){
        this.name = name;
        return this;
    }
    public String getName(){
        return name;
    }

    public WaterInfo setWater(Calen water){
        this.water = water;
        return this;
    }
    public Calen getWater(){
        return water;
    }

    public WaterInfo setFertilize(Calen fertilize){
        this.fertilize = fertilize;
        return this;
    }
    public Calen getFertilize(){
        return fertilize;
    }

    @Override
    public String toString() {
        return name + "\n" +
                water + "\n" +
                fertilize + "\n";
    }
}
