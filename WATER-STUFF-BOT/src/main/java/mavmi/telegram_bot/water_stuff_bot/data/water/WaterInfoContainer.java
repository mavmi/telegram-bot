package mavmi.telegram_bot.water_stuff_bot.data.water;

import lombok.Getter;
import org.springframework.lang.Nullable;

import java.util.*;

public class WaterInfoContainer {
    @Getter
    private final Long userId;
    private final List<WaterInfo> userWaterDataList;

    public WaterInfoContainer(Long userId) {
        this.userId = userId;
        this.userWaterDataList = new ArrayList<>();
    }

    @Nullable
    public WaterInfo get(String name) {
        Optional<WaterInfo> opt = userWaterDataList
                .stream()
                .filter(value -> value.getName().equals(name))
                .findFirst();

        return opt.orElse(null);
    }

    public boolean put(WaterInfo waterInfo) {
        Optional<WaterInfo> opt = userWaterDataList
                .stream()
                .filter(value -> value.getName().equals(waterInfo.getName()))
                .findFirst();

        if (opt.isPresent()) {
            return false;
        } else {
            userWaterDataList.add(waterInfo);
            return true;
        }
    }

    public boolean remove(String name) {
        return userWaterDataList.removeIf(value -> value.getName().equals(name));
    }

    public List<WaterInfo> asList() {
        return userWaterDataList;
    }

    public int size() {
        return userWaterDataList.size();
    }
}
