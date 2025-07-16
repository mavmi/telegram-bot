package mavmi.telegram_bot.shakal.service.database;

import mavmi.telegram_bot.lib.database_starter.model.RequestModel;
import mavmi.telegram_bot.lib.database_starter.model.UserModel;
import mavmi.telegram_bot.shakal.service.database.dto.ShakalRequestDto;
import mavmi.telegram_bot.shakal.service.database.dto.ShakalUserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShakalMapper {
    ShakalRequestDto requestModelToDto(RequestModel model);
    RequestModel requestDtoToModel(ShakalRequestDto dto);
    ShakalUserDto userModelToDto(UserModel model);
    UserModel userDtoToModel(ShakalUserDto dto);
}
