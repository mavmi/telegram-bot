package mavmi.telegram_bot.monitoring.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceFileJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class HttpClient {

    private OkHttpClient httpClient;
    private ObjectMapper objectMapper;

    public final String telegramBotUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendFileEndpoint;

    public HttpClient(
            @Value("${service.telegram-bot.url}") String telegramBotUrl,
            @Value("${service.telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${service.telegram-bot.endpoint.sendFile}") String telegramBotSendFileEndpoint
    ) {
        this.httpClient = new OkHttpClient();
        this.objectMapper = new ObjectMapper();

        this.telegramBotUrl = telegramBotUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendFileEndpoint = telegramBotSendFileEndpoint;
    }

    public void sendText(
            List<Long> chatIdx,
            String msg
    ) {
        sendRequest(
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

    public void sendFile(
            List<Long> chatIdx,
            String filePath
    ) {
        sendRequest(
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
