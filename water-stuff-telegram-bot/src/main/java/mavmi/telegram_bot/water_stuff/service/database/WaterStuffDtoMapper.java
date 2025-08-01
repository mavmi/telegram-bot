package mavmi.telegram_bot.water_stuff.service.database;

import mavmi.telegram_bot.lib.database_starter.model.WaterModel;
import mavmi.telegram_bot.water_stuff.service.database.dto.WaterStuffDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WaterStuffDtoMapper {
    WaterStuffDto dbModelToServiceDto(WaterModel model);
    WaterModel serviceDtoToDbModel(WaterStuffDto dto);
}
