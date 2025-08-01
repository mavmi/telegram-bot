package mavmi.telegram_bot.rocketchat.service.database;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.lib.database_starter.repository.RocketchatRepository;
import mavmi.telegram_bot.rocketchat.service.database.dto.RocketchatDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RocketchatDatabaseService {

    private final RocketchatDtoMapper mapper;
    private final RocketchatRepository repository;

    public void updateByTelegramId(RocketchatDto dto) {
        repository.updateByTelegramId(mapper.dtoToModel(dto));
    }

    public void deleteByTelegramId(long telegramId) {
        repository.deleteByTelegramId(telegramId);
    }

    public void save(RocketchatDto dto) {
        repository.save(mapper.dtoToModel(dto));
    }

    @Nullable
    public RocketchatDto findByTelegramId(long telegramId) {
        Optional<RocketchatModel> optional = repository.findByTelegramId(telegramId);
        return optional.map(mapper::modelToDto).orElse(null);
    }
}
