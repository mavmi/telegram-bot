package mavmi.telegram_bot.shakal.mapper;

import com.pengrad.telegrambot.model.Dice;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import mavmi.telegram_bot.common.service.dto.common.DiceJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.UserJson;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import org.mapstruct.*;

import java.util.Date;

@Mapper(componentModel = "spring", nullValueMapMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface RequestsMapper {

    @Mapping(target = "textMessage", expression = "java(message.text())")
    @Mapping(target = "date", expression = "java(RequestsMapper.dateFromLong(message.date().longValue()))")
    MessageJson telegramMessageToMessageJson(Message message);

    @Mapping(target = "id", expression = "java(user.id())")
    @Mapping(target = "username", expression = "java(user.username())")
    @Mapping(target = "firstName", expression = "java(user.firstName())")
    @Mapping(target = "lastName", expression = "java(user.lastName())")
    UserJson telegramUserToUserJson(User user);

    @Mapping(target = "userDiceValue", expression = "java(dice.value())")
    DiceJson telegramDiceToDiceJson(Dice dice);

    @Mapping(target = "chatId", expression = "java(message.from().id())")
    @Mapping(target = "userJson", expression = "java(telegramUserToUserJson(message.from()))")
    @Mapping(target = "messageJson", source = "message")
    @Mapping(target = "diceJson", expression = "java(telegramDiceToDiceJson(message.dice()))")
    ShakalServiceRq telegramRequestToShakalServiceRequest(Message message);

    static Date dateFromLong(long time) {
        return new Date(time);
    }
}
