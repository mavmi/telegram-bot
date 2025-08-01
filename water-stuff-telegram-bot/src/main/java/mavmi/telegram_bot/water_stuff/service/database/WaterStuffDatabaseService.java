package mavmi.telegram_bot.water_stuff.service.database;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.lib.database_starter.repository.WaterRepository;
import mavmi.telegram_bot.lib.service_api.database.DatabaseService;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class WaterStuffDatabaseService implements DatabaseService {

    private final WaterStuffDtoMapper mapper;
    private final WaterRepository repository;

    public List<WaterStuffDto> findByUserId(long userId) {
        return repository.findByUserId(userId)
                .stream()
                .map(mapper::dbModelToServiceDto)
                .collect(Collectors.toList());
    }

    @Nullable
    public WaterStuffDto findByUserIdAndGroupName(long userId, String groupName) {
        Optional<WaterModel> optional = repository.findByUserIdAndGroupName(userId, groupName);
        return optional.map(mapper::dbModelToServiceDto).orElse(null);
    }

    public void updateByUserIdAndGroupName(WaterStuffDto dto) {
        repository.updateByUserIdAndGroupName(mapper.serviceDtoToDbModel(dto));
    }

    public void removeByUserIdAndGroupName(long userId, String groupName) {
        repository.removeByUserIdAndGroupName(userId, groupName);
    }

    public void save(WaterStuffDto dto) {
        repository.save(mapper.serviceDtoToDbModel(dto));
    }

    public List<WaterStuffDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::dbModelToServiceDto)
                .collect(Collectors.toList());
    }
}
