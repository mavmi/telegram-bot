package mavmi.telegram_bot.shakal.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceKeyboardJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class HttpClient {

    private OkHttpClient httpClient;
    private ObjectMapper objectMapper;

    public final String telegramBotUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendKeyboardEndpoint;
    public final String telegramBotSendDiceEndpoint;

    public HttpClient(
            @Value("${service.telegram-bot.url}") String telegramBotUrl,
            @Value("${service.telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${service.telegram-bot.endpoint.sendKeyboard}") String telegramBotSendKeyboardEndpoint,
            @Value("${service.telegram-bot.endpoint.sendDice}") String telegramBotSendDiceEndpoint
    ) {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();

        this.telegramBotUrl = telegramBotUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendKeyboardEndpoint = telegramBotSendKeyboardEndpoint;
        this.telegramBotSendDiceEndpoint = telegramBotSendDiceEndpoint;
    }

    public void sendText(
            long chatId,
            String text
    ) {
        sendRequest(
                telegramBotSendTextEndpoint,
                ServiceRequestJson
                        .builder()
                        .chatId(chatId)
                        .serviceMessageJson(
                                ServiceMessageJson
                                        .builder()
                                        .textMessage(text)
                                        .build()
                        )
                        .build()
        );
    }

    public void sendDice(
            long chatId,
            String msg,
            String[] buttons
    ) {
        sendRequest(
                telegramBotSendDiceEndpoint,
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

    public void sendKeyboard(
            long chatId,
            String msg,
            String[] buttons
    ) {
        sendRequest(
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

    public void sendRequest(
            String endpoint,
            ServiceRequestJson serviceRequestJson
    ) {
        try {
            String requestBodyStr = objectMapper.writeValueAsString(serviceRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request request = new Request.Builder()
                    .url(telegramBotUrl + endpoint)
                    .post(requestBody)
                    .build();

            httpClient.newCall(request).execute();
        } catch (JsonProcessingException e) {
            log.error("Error while converting to json");
            e.printStackTrace(System.err);
        } catch (IOException e) {
            log.error("Error while sending HTTP request");
            e.printStackTrace(System.err);
        }
    }
}
