package mavmi.telegram_bot.rocketchat.mapper;

import mavmi.telegram_bot.common.database.model.RocketchatModel;
import mavmi.telegram_bot.rocketchat.cache.RocketDataCache;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RocketchatMapper {

    @Mapping(target = "userId", expression = "java(rocketchatModel.getTelegramId())")
    @Mapping(target = "username", expression = "java(rocketchatModel.getTelegramUsername())")
    @Mapping(target = "firstName", expression = "java(rocketchatModel.getTelegramFirstname())")
    @Mapping(target = "lastName", expression = "java(rocketchatModel.getTelegramLastname())")
    @Mapping(target = "rocketchatUsername", expression = "java(rocketchatModel.getRocketchatUsername())")
    @Mapping(target = "rocketchatPasswordHash", expression = "java(rocketchatModel.getRocketchatPasswordHash())")
    @Mapping(target = "rocketchatToken", expression = "java(rocketchatModel.getRocketchatToken())")
    @Mapping(target = "rocketchatTokenExpiryDate", expression = "java(rocketchatModel.getRocketchatTokenExpiryDate())")
    RocketDataCache rocketchatDatabaseModelToRocketchatDataCache(RocketchatModel rocketchatModel);
}
