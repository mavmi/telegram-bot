package mavmi.telegram_bot.monitoring.service.http;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceFileJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceKeyboardJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceTaskManagerJson;
import mavmi.telegram_bot.common.http.AbsHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient<ServiceRequestJson> {

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

    public int sendText(
            List<Long> chatIdx,
            String msg
    ) {
        return sendRequest(
                telegramBotUrl,
                telegramBotSendTextEndpoint,
                ServiceRequestJson
                        .builder()
                        .chatIdx(chatIdx)
                        .serviceMessageJson(
                                ServiceMessageJson
                                        .builder()
                                        .textMessage(msg)
                                        .build()
                        )
                        .build()
        );
    }

    public int sendFile(
            List<Long> chatIdx,
            String filePath
    ) {
        return sendRequest(
                telegramBotUrl,
                telegramBotSendFileEndpoint,
                ServiceRequestJson
                        .builder()
                        .chatIdx(chatIdx)
                        .serviceFileJson(
                                ServiceFileJson
                                        .builder()
                                        .filePath(filePath)
                                        .build()
                        )
                        .build()
        );
    }

    public int sendKeyboard(
            long chatId,
            String msg,
            String[] buttons
    ) {
        return sendRequest(
                telegramBotUrl,
                telegramBotSendKeyboardEndpoint,
                ServiceRequestJson
                        .builder()
                        .chatId(chatId)
                        .serviceMessageJson(
                                ServiceMessageJson
                                        .builder()
                                        .textMessage(msg)
                                        .build()
                        )
                        .serviceKeyboardJson(
                                ServiceKeyboardJson
                                        .builder()
                                        .keyboardButtons(buttons)
                                        .build()
                        )
                        .build()
        );
    }

    public int sendPutTask(
            long id,
            String target,
            String message
    ) {
        return sendRequest(
                asyncTaskManagerUrl,
                asyncTaskManagerPutEndpoint,
                ServiceRequestJson
                        .builder()
                        .chatId(id)
                        .serviceTaskManagerJson(
                                ServiceTaskManagerJson
                                        .builder()
                                        .target(target)
                                        .message(message)
                                        .build()
                        )
                        .build()
        );
    }
}
