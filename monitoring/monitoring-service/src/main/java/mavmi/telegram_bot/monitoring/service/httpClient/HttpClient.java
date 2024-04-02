package mavmi.telegram_bot.monitoring.service.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRq;
import mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRs;
import mavmi.telegram_bot.common.httpClient.AbstractHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HttpClient extends AbstractHttpClient {

    public final String telegramBotUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendFileEndpoint;

    public HttpClient(
            SslBundles sslBundles,
            RestTemplateBuilder restTemplateBuilder,
            @Value("${telegram-bot.url}") String telegramBotUrl,
            @Value("${telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${telegram-bot.endpoint.sendFile}") String telegramBotSendFileEndpoint
    ) {
        super(sslBundles.getBundle("service"), restTemplateBuilder);
        this.telegramBotUrl = telegramBotUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendFileEndpoint = telegramBotSendFileEndpoint;
    }

    @Nullable
    @SneakyThrows
    public ResponseEntity<MonitoringTelegramBotRs> sendText(
            List<Long> chatIdx,
            String msg
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        MonitoringTelegramBotRq monitoringTelegramBotRq = MonitoringTelegramBotRq
                .builder()
                .chatIdx(chatIdx)
                .messageJson(messageJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringTelegramBotRq);

        return sendPostRequest(
                telegramBotUrl,
                telegramBotSendTextEndpoint,
                requestBody,
                MonitoringTelegramBotRs.class
        );
    }

    @Nullable
    @SneakyThrows
    public ResponseEntity<MonitoringTelegramBotRs> sendFile(
            List<Long> chatIdx,
            String filePath
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        FileJson fileJson = FileJson
                .builder()
                .filePath(filePath)
                .build();

        MonitoringTelegramBotRq monitoringTelegramBotRq = MonitoringTelegramBotRq
                .builder()
                .chatIdx(chatIdx)
                .fileJson(fileJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringTelegramBotRq);

        return sendPostRequest(
                telegramBotUrl,
                telegramBotSendFileEndpoint,
                requestBody,
                MonitoringTelegramBotRs.class
        );
    }
}
