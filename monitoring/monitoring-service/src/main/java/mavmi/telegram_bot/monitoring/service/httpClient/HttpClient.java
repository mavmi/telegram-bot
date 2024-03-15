package mavmi.telegram_bot.monitoring.service.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRq;
import mavmi.telegram_bot.common.httpClient.AbstractHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HttpClient extends AbstractHttpClient {

    public final String telegramBotUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendFileEndpoint;

    public HttpClient(
            @Value("${telegram-bot.url}") String telegramBotUrl,
            @Value("${telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${telegram-bot.endpoint.sendFile}") String telegramBotSendFileEndpoint
    ) {
        this.telegramBotUrl = telegramBotUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendFileEndpoint = telegramBotSendFileEndpoint;
    }

    @SneakyThrows
    public Response sendText(
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
                requestBody
        );
    }

    @SneakyThrows
    public Response sendFile(
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
                requestBody
        );
    }
}
