package mavmi.telegram_bot.rocketchat.service.database;

import mavmi.telegram_bot.lib.database_starter.model.RocketchatModel;
import mavmi.telegram_bot.rocketchat.service.database.dto.RocketchatDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RocketchatDtoMapper {
    RocketchatDto modelToDto(RocketchatModel model);
    RocketchatModel dtoToModel(RocketchatDto dto);
}
