package mavmi.telegram_bot.lib.metric_starter.service.database;

import mavmi.telegram_bot.lib.database_starter.model.MetricModel;
import mavmi.telegram_bot.lib.metric_starter.service.database.dto.MetricDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MetricDatabaseMapper {

    MetricDto modelToDto(MetricModel model);
    MetricModel dtoToModel(MetricDto dto);
}
