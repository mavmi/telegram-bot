package mavmi.telegram_bot.lib.metric_starter.service.database;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.model.MetricModel;
import mavmi.telegram_bot.lib.database_starter.repository.MetricRepository;
import mavmi.telegram_bot.lib.metric_starter.service.database.dto.MetricDto;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class MetricDatabaseService {

    private final MetricDatabaseMapper mapper;
    private final MetricRepository repository;

    public void updateByTelegramIdAndDate(MetricDto dto) {
        repository.updateByTelegramIdAndDate(mapper.dtoToModel(dto));
    }

    @Nullable
    public MetricDto find(String botName, long telegramId, Date date) {
        Optional<MetricModel> optional = repository.find(botName, telegramId, date);
        return optional.map(mapper::modelToDto).orElse(null);
    }

    public void save(MetricDto dto) {
        repository.save(mapper.dtoToModel(dto));
    }
}
