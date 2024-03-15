package mavmi.telegram_bot.water_stuff.telegram_bot.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
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
    public final String waterStuffServiceRequestEndpoint;
    public final String reminderServiceRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.waterStuffServiceRequest}") String waterStuffServiceRequestEndpoint,
            @Value("${service.endpoint.reminderServiceRequest}") String reminderServiceRequestEndpoint
    ){
        this.serviceUrl = serviceUrl;
        this.waterStuffServiceRequestEndpoint = waterStuffServiceRequestEndpoint;
        this.reminderServiceRequestEndpoint = reminderServiceRequestEndpoint;
    }

    @SneakyThrows
    public Response waterStuffServiceRequest(Message telegramMessage) {
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

        WaterStuffServiceRq waterStuffServiceRq = WaterStuffServiceRq
                .builder()
                .chatId(telegramMessage.chat().id())
                .userJson(userJson)
                .messageJson(messageJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(waterStuffServiceRq);

        return sendPostRequest(
                serviceUrl,
                waterStuffServiceRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, Long.toString(telegramUser.id())),
                requestBody
        );
    }

    public Response reminderServiceRequest() {
        return sendGetRequest(serviceUrl, reminderServiceRequestEndpoint);
    }
}
