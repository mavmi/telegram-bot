package mavmi.telegram_bot.rocketchat.mapper;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.UserJson;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper from telegram message to service dto
 */
@Mapper(componentModel = "spring")
public interface RequestsMapper {

    @Mapping(target = "id", expression = "java(user.id())")
    @Mapping(target = "username", expression = "java(user.username())")
    @Mapping(target = "firstName", expression = "java(user.firstName())")
    @Mapping(target = "lastName", expression = "java(user.lastName())")
    UserJson telegramUserToUserJson(User user);

    @Mapping(target = "msgId", expression = "java(message.messageId())")
    @Mapping(target = "textMessage", expression = "java(message.text())")
    MessageJson telegramMessageToMessageJson(Message message);

    @Mapping(target = "chatId", expression = "java(message.chat().id())")
    @Mapping(target = "userJson", expression = "java(telegramUserToUserJson(message.from()))")
    @Mapping(target = "messageJson", source = "message")
    RocketchatServiceRq telegramRequestToRocketchatServiceRequest(Message message);
}
