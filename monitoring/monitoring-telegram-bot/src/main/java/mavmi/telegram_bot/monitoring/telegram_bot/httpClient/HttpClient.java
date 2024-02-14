package mavmi.telegram_bot.monitoring.telegram_bot.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.TaskManagerJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.common.UserMessageJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.httpClient.AbstractHttpClient;
import mavmi.telegram_bot.common.httpFilter.HttpRequestFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class HttpClient extends AbstractHttpClient {

    public final String serviceUrl;
    public final String processRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.processRequest}") String processRequestEndpoint
    ) {
        this.serviceUrl = serviceUrl;
        this.processRequestEndpoint = processRequestEndpoint;
    }

    @SneakyThrows
    public int processRequest(Message telegramMessage, String target) {
        ObjectMapper objectMapper = new ObjectMapper();
        User telegramUser = telegramMessage.from();

        UserJson userJson = UserJson
                .builder()
                .id(telegramUser.id())
                .username(telegramUser.username())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .build();

        UserMessageJson userMessageJson = UserMessageJson
                .builder()
                .textMessage(telegramMessage.text())
                .build();

        TaskManagerJson taskManagerJson = TaskManagerJson
                .builder()
                .target(target)
                .message(telegramMessage.text())
                .build();

        MonitoringServiceRq monitoringServiceRq = MonitoringServiceRq
                .builder()
                .chatId(telegramMessage.chat().id())
                .userJson(userJson)
                .userMessageJson(userMessageJson)
                .taskManagerJson(taskManagerJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringServiceRq);

        return sendRequest(
                serviceUrl,
                processRequestEndpoint,
                Map.of(HttpRequestFilter.ID_HEADER_NAME, Long.toString(telegramUser.id())),
                requestBody
        );
    }
}
