package mavmi.telegram_bot.water_stuff.mapper;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import mavmi.telegram_bot.lib.dto.service.common.CallbackQueryJson;
import mavmi.telegram_bot.lib.dto.service.common.MessageJson;
import mavmi.telegram_bot.lib.dto.service.common.UserJson;
import mavmi.telegram_bot.water_stuff.service.waterStuff.dto.WaterStuffServiceRq;
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

    @Mapping(target = "textMessage", expression = "java(message.text())")
    MessageJson telegramMessageToMessageJson(Message message);

    @Mapping(target = "chatId", expression = "java(message.chat().id())")
    @Mapping(target = "userJson", expression = "java(telegramUserToUserJson(message.from()))")
    @Mapping(target = "messageJson", source = "message")
    WaterStuffServiceRq telegramRequestToWaterStuffServiceRequest(Message message);

    @Mapping(target = "data", expression = "java(callbackQuery.data())")
    @Mapping(target = "messageId", expression = "java(callbackQuery.maybeInaccessibleMessage().messageId())")
    CallbackQueryJson telegramCallBackQueryToCallbackQueryJson(CallbackQuery callbackQuery);

    @Mapping(target = "chatId", expression = "java(callbackQuery.from().id())")
    @Mapping(target = "callbackQueryJson", source = "callbackQuery")
    WaterStuffServiceRq telegramCallBackQueryToWaterStuffServiceRequest(CallbackQuery callbackQuery);
}
