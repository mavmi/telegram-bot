package mavmi.telegram_bot.monitoring.service.database;

import mavmi.telegram_bot.lib.database_starter.model.RuleModel;
import mavmi.telegram_bot.monitoring.service.database.dto.RuleDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MonitoringDatabaseMapper {
    RuleDto modelToDto(RuleModel model);
    RuleModel dtoToModel(RuleDto dto);
}
