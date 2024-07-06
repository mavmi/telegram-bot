package mavmi.telegram_bot.water_stuff.data.water;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class WaterInfoContainer {

    @JsonProperty("user_id")
    private Long userId;
    @JsonProperty("water_data_list")
    private List<WaterInfo> userWaterDataList;

    public WaterInfoContainer(Long userId) {
        this.userId = userId;
        this.userWaterDataList = new ArrayList<>();
    }

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
