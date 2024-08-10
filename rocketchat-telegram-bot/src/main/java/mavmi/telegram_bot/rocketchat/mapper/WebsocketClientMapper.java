package mavmi.telegram_bot.rocketchat.mapper;

import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.ConnectRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.CreateDMRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.LoginRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.SubscribeForMsgUpdatesRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rq.LoginParameter;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rq.LoginParameterPassword;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.inner.login.rq.LoginParameterUser;
import mavmi.telegram_bot.rocketchat.utils.Utils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WebsocketClientMapper {

    @Mapping(target = "msg", expression = "java(\"method\")")
    @Mapping(target = "method", expression = "java(\"createDirectMessage\")")
    @Mapping(target = "id", expression = "java(WebsocketClientMapper.generateRandomId())")
    @Mapping(target = "params", expression = "java(new String[]{ username })")
    CreateDMRq generateCreateDmRequest(String username);

    @Mapping(target = "msg", expression = "java(\"connect\")")
    @Mapping(target = "version", expression = "java(\"1\")")
    @Mapping(target = "support", expression = "java(new String[]{ \"1\" })")
    ConnectRq generateConnectRequest(String empty);

    @Mapping(target = "username", source = "username")
    LoginParameterUser generateLoginParameterUser(String username);

    @Mapping(target = "digest", source = "passwordHash")
    @Mapping(target = "algorithm", expression = "java(\"sha-256\")")
    LoginParameterPassword generateLoginParameterPassword(String passwordHash);

    @Mapping(target = "user", expression = "java(generateLoginParameterUser(username))")
    @Mapping(target = "password", expression = "java(generateLoginParameterPassword(passwordHash))")
    LoginParameter generateLoginParameter(String username, String passwordHash);

    @Mapping(target = "msg", expression = "java(\"method\")")
    @Mapping(target = "method", expression = "java(\"login\")")
    @Mapping(target = "id", expression = "java(WebsocketClientMapper.generateRandomId())")
    @Mapping(target = "params", expression = "java(WebsocketClientMapper.getLoginParams(generateLoginParameter(username, passwordHash)))")
    LoginRq generateLoginRequest(String username, String passwordHash);

    @Mapping(target = "msg", expression = "java(\"sub\")")
    @Mapping(target = "id", expression = "java(WebsocketClientMapper.generateRandomId())")
    @Mapping(target = "name", expression = "java(\"stream-notify-user\")")
    @Mapping(target = "params", expression = "java(WebsocketClientMapper.getSubscribeParams(rocketchatUserId + \"/message\"))")
    SubscribeForMsgUpdatesRq generateSubscribeForMsgUpdatesRequest(String rocketchatUserId);

    static String generateRandomId() {
        return Utils.generateRandomString();
    }

    static List<LoginParameter> getLoginParams(LoginParameter loginParameter) {
        return List.of(loginParameter);
    }

    static List<Object> getSubscribeParams(String rocketchatUserId) {
        return SubscribeForMsgUpdatesRq.createParams(rocketchatUserId, false);
    }
}
