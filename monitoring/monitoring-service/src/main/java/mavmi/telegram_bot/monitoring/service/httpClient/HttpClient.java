package mavmi.telegram_bot.monitoring.service.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
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
    public final String telegramBotSendKeyboardEndpoint;

    public HttpClient(
            @Value("${telegram-bot.url}") String telegramBotUrl,
            @Value("${telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${telegram-bot.endpoint.sendFile}") String telegramBotSendFileEndpoint,
            @Value("${telegram-bot.endpoint.sendKeyboard}") String telegramBotSendKeyboardEndpoint
    ) {
        this.telegramBotUrl = telegramBotUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendFileEndpoint = telegramBotSendFileEndpoint;
        this.telegramBotSendKeyboardEndpoint = telegramBotSendKeyboardEndpoint;
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

        return sendRequest(
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

        return sendRequest(
                telegramBotUrl,
                telegramBotSendFileEndpoint,
                requestBody
        );
    }

    @SneakyThrows
    public Response sendKeyboard(
            long chatId,
            String msg,
            String[] buttons
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(buttons)
                .build();

        MonitoringTelegramBotRq monitoringTelegramBotRq = MonitoringTelegramBotRq
                .builder()
                .chatId(chatId)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringTelegramBotRq);

        return sendRequest(
                telegramBotUrl,
                telegramBotSendKeyboardEndpoint,
                requestBody
        );
    }
}
