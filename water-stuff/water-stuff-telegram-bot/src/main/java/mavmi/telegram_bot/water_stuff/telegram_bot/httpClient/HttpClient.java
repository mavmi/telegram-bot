package mavmi.telegram_bot.water_stuff.telegram_bot.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.impl.water_stuff.service.WaterStuffServiceDtoRq;
import mavmi.telegram_bot.common.httpClient.AbstractHttpClient;
import mavmi.telegram_bot.common.httpFilter.UserSessionHttpFilter;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class HttpClient extends AbstractHttpClient {

    public final String serviceUrl;
    public final String serviceProcessRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.processRequest}") String serviceProcessRequestEndpoint
    ){
        this.serviceUrl = serviceUrl;
        this.serviceProcessRequestEndpoint = serviceProcessRequestEndpoint;
    }

    @SneakyThrows
    public Response processRequest(Message telegramMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        User telegramUser = telegramMessage.from();

        UserJson userJson = UserJson
                .builder()
                .id(telegramUser.id())
                .username(telegramUser.username())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .build();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(telegramMessage.text())
                .build();

        WaterStuffServiceDtoRq waterStuffServiceDtoRq = WaterStuffServiceDtoRq
                .builder()
                .chatId(telegramMessage.chat().id())
                .userJson(userJson)
                .messageJson(messageJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(waterStuffServiceDtoRq);

        return sendRequest(
                serviceUrl,
                serviceProcessRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, Long.toString(telegramUser.id())),
                requestBody
        );
    }
}
