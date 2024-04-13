package mavmi.telegram_bot.monitoring.telegram_bot.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.AsyncTaskManagerJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.httpClient.HttpClient;
import mavmi.telegram_bot.common.httpFilter.userSession.UserSessionHttpFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class MonitoringTelegramBotHttpClient extends HttpClient {

    public final String serviceUrl;
    public final String processRequestEndpoint;

    public MonitoringTelegramBotHttpClient(
            SslBundles sslBundles,
            RestTemplateBuilder restTemplateBuilder,
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.monitoringServiceRequest}") String processRequestEndpoint
    ) {
        super(sslBundles.getBundle("telegram-bot"), restTemplateBuilder);
        this.serviceUrl = serviceUrl;
        this.processRequestEndpoint = processRequestEndpoint;
    }

    @Nullable
    @SneakyThrows
    public ResponseEntity<MonitoringServiceRs> monitoringServiceRequest(Message telegramMessage, String target) {
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

        AsyncTaskManagerJson asyncTaskManagerJson = AsyncTaskManagerJson
                .builder()
                .target(target)
                .message(telegramMessage.text())
                .build();

        MonitoringServiceRq monitoringServiceRq = MonitoringServiceRq
                .builder()
                .chatId(telegramMessage.chat().id())
                .userJson(userJson)
                .messageJson(messageJson)
                .asyncTaskManagerJson(asyncTaskManagerJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringServiceRq);

        return sendPostRequest(
                serviceUrl,
                processRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, Long.toString(telegramUser.id())),
                requestBody,
                MonitoringServiceRs.class
        );
    }
}
