package mavmi.telegram_bot.monitoring.mapper;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import mavmi.telegram_bot.common.service.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.service.dto.common.MessageJson;
import mavmi.telegram_bot.common.service.dto.common.UserJson;
import mavmi.telegram_bot.monitoring.service.monitoring.dto.monitoringService.MonitoringServiceRq;
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

    @Mapping(target = "target", source = "target")
    @Mapping(target = "message", expression = "java(message.text())")
    AsyncTaskManagerJson telegramMessageToAsyncTaskManagerJson(Message message, String target);

    @Mapping(target = "chatId", expression = "java(message.from().id())")
    @Mapping(target = "userJson", expression = "java(telegramUserToUserJson(message.from()))")
    @Mapping(target = "messageJson", source = "message")
    @Mapping(target = "asyncTaskManagerJson", expression = "java(telegramMessageToAsyncTaskManagerJson(message, target))")
    MonitoringServiceRq telegramMessageToMonitoringServiceRequest(Message message, String target);
}
