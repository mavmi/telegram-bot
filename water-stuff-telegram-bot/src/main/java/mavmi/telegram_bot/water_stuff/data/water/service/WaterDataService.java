package mavmi.telegram_bot.water_stuff.data.water.service;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.lib.database_starter.repository.WaterRepository;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class WaterDataService {

    private final WaterRepository repository;

    public WaterModel get(Long userId, String name) {
        return repository.findByUserIdAndGroupName(userId, name).orElse(null);
    }

    public List<WaterModel> getAll(Long userId) {
        return repository.findByUserId(userId);
    }

    public void put(WaterModel waterModel) {
        if (repository.findByUserIdAndGroupName(waterModel.getUserId(), waterModel.getName()).isPresent()) {
            repository.updateByUserIdAndGroupName(waterModel);
        } else {
            repository.save(waterModel);
        }
    }

    public void remove(Long userId, String name) {
        repository.removeByUserIdAndGroupName(userId, name);
    }

    public int size(Long userId) {
        return repository.findByUserId(userId).size();
    }

    public List<Long> getUsersIdx() {
        Set<Long> set = new HashSet<>();

        for (WaterModel model : repository.findAll()) {
            set.add(model.getUserId());
        }

        return new ArrayList<>(set);
    }
}
