package mavmi.telegram_bot.monitoring.service.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.TaskManagerJson;
import mavmi.telegram_bot.common.dto.common.UserMessageJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRq;
import mavmi.telegram_bot.common.dto.impl.task_manager.TaskManagerRq;
import mavmi.telegram_bot.common.httpClient.AbstractHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HttpClient extends AbstractHttpClient {

    public final String telegramBotUrl;
    public final String asyncTaskManagerUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendFileEndpoint;
    public final String telegramBotSendKeyboardEndpoint;
    public final String asyncTaskManagerGetNextEndpoint;
    public final String asyncTaskManagerPutEndpoint;

    public HttpClient(
            @Value("${telegram-bot.url}") String telegramBotUrl,
            @Value("${async-task-manager.url}") String asyncTaskManagerUrl,
            @Value("${telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${telegram-bot.endpoint.sendFile}") String telegramBotSendFileEndpoint,
            @Value("${telegram-bot.endpoint.sendKeyboard}") String telegramBotSendKeyboardEndpoint,
            @Value("${async-task-manager.endpoint.getNext}") String asyncTaskManagerGetNextEndpoint,
            @Value("${async-task-manager.endpoint.put}") String asyncTaskManagerPutEndpoint
    ) {
        this.telegramBotUrl = telegramBotUrl;
        this.asyncTaskManagerUrl = asyncTaskManagerUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendFileEndpoint = telegramBotSendFileEndpoint;
        this.telegramBotSendKeyboardEndpoint = telegramBotSendKeyboardEndpoint;
        this.asyncTaskManagerGetNextEndpoint = asyncTaskManagerGetNextEndpoint;
        this.asyncTaskManagerPutEndpoint = asyncTaskManagerPutEndpoint;
    }

    @SneakyThrows
    public int sendText(
            List<Long> chatIdx,
            String msg
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        UserMessageJson userMessageJson = UserMessageJson
                .builder()
                .textMessage(msg)
                .build();

        MonitoringTelegramBotRq monitoringTelegramBotRq = MonitoringTelegramBotRq
                .builder()
                .chatIdx(chatIdx)
                .userMessageJson(userMessageJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringTelegramBotRq);

        return sendRequest(
                telegramBotUrl,
                telegramBotSendTextEndpoint,
                requestBody
        );
    }

    @SneakyThrows
    public int sendFile(
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
    public int sendKeyboard(
            long chatId,
            String msg,
            String[] buttons
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        UserMessageJson userMessageJson = UserMessageJson
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
                .userMessageJson(userMessageJson)
                .keyboardJson(keyboardJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(monitoringTelegramBotRq);

        return sendRequest(
                telegramBotUrl,
                telegramBotSendKeyboardEndpoint,
                requestBody
        );
    }

    @SneakyThrows
    public int sendPutTask(
            long id,
            String target,
            String message
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        TaskManagerJson taskManagerJson = TaskManagerJson
                .builder()
                .message(message)
                .target(target)
                .build();

        TaskManagerRq taskManagerRq = TaskManagerRq
                .builder()
                .chatId(id)
                .taskManagerJson(taskManagerJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(taskManagerRq);

        return sendRequest(
                asyncTaskManagerUrl,
                asyncTaskManagerPutEndpoint,
                requestBody
        );
    }
}
