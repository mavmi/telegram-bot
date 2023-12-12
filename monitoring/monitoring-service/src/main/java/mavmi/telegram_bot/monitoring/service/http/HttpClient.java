package mavmi.telegram_bot.monitoring.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceFileJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceTaskManagerJson;
import mavmi.telegram_bot.common.utils.http.AbsHttpClient;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.List;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient<ServiceRequestJson> {

    public final String telegramBotUrl;
    public final String asyncTaskManagerUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendFileEndpoint;
    public final String asyncTaskManagerGetNextEndpoint;
    public final String asyncTaskManagerPutEndpoint;

    public HttpClient(
            @Value("${telegram-bot.url}") String telegramBotUrl,
            @Value("${async-task-manager.url}") String asyncTaskManagerUrl,
            @Value("${telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${telegram-bot.endpoint.sendFile}") String telegramBotSendFileEndpoint,
            @Value("${async-task-manager.endpoint.getNext}") String asyncTaskManagerGetNextEndpoint,
            @Value("${async-task-manager.endpoint.put}") String asyncTaskManagerPutEndpoint
    ) {
        this.telegramBotUrl = telegramBotUrl;
        this.asyncTaskManagerUrl = asyncTaskManagerUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendFileEndpoint = telegramBotSendFileEndpoint;
        this.asyncTaskManagerGetNextEndpoint = asyncTaskManagerGetNextEndpoint;
        this.asyncTaskManagerPutEndpoint = asyncTaskManagerPutEndpoint;
    }

    public int sendText(
            List<Long> chatIdx,
            String msg
    ) {
        return sendRequest(
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

    public int sendPutTask(
            String target,
            String message
    ) {
        return sendRequest(
                asyncTaskManagerUrl,
                asyncTaskManagerPutEndpoint,
                ServiceRequestJson
                        .builder()
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

    @Override
    public int sendRequest(
            String endpoint,
            ServiceRequestJson serviceRequestJson
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient httpClient = new OkHttpClient();

        try {
            String requestBodyStr = objectMapper.writeValueAsString(serviceRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request request = new Request.Builder()
                    .url(telegramBotUrl + endpoint)
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            return response.code();
        } catch (JsonProcessingException e) {
            log.error("Error while converting to json");
            e.printStackTrace(System.out);
            return HttpURLConnection.HTTP_UNAVAILABLE;
        } catch (IOException e) {
            log.error("Error while sending HTTP request");
            e.printStackTrace(System.out);
            return HttpURLConnection.HTTP_UNAVAILABLE;
        }
    }

    public int sendRequest(
            String url,
            String endpoint,
            ServiceRequestJson serviceRequestJson
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient httpClient = new OkHttpClient();

        try {
            String requestBodyStr = objectMapper.writeValueAsString(serviceRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request request = new Request.Builder()
                    .url(url + endpoint)
                    .post(requestBody)
                    .build();

            Response response = httpClient.newCall(request).execute();
            return response.code();
        } catch (JsonProcessingException e) {
            log.error("Error while converting to json");
            e.printStackTrace(System.out);
            return HttpURLConnection.HTTP_UNAVAILABLE;
        } catch (IOException e) {
            log.error("Error while sending HTTP request");
            e.printStackTrace(System.out);
            return HttpURLConnection.HTTP_UNAVAILABLE;
        }
    }
}
