package mavmi.telegram_bot.monitoring.service.database;

import lombok.RequiredArgsConstructor;
import mavmi.telegram_bot.lib.database_starter.repository.RuleRepository;
import mavmi.telegram_bot.monitoring.service.database.dto.RuleDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MonitoringDatabaseService {

    private final MonitoringDatabaseMapper mapper;
    private final RuleRepository repository;

    public List<RuleDto> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::modelToDto)
                .collect(Collectors.toList());
    }
}
