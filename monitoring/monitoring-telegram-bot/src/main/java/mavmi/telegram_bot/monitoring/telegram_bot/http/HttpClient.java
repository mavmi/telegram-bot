package mavmi.telegram_bot.monitoring.telegram_bot.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.utils.http.AbsHttpClient;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient<BotRequestJson> {

    public final String serviceUrl;
    public final String putTaskEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.putTask}") String putTaskEndpoint
    ) {
        this.serviceUrl = serviceUrl;
        this.putTaskEndpoint = putTaskEndpoint;
    }

    public int putTask(Long id, String target, String message) {
        return sendRequest(
                putTaskEndpoint,
                BotRequestJson
                        .builder()
                        .chatId(id)
                        .botTaskManagerJson(
                                BotTaskManagerJson
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
            BotRequestJson botRequestJson
    ) {
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient httpClient = new OkHttpClient();

        try {
            String requestBodyStr = objectMapper.writeValueAsString(botRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request request = new Request.Builder()
                    .url(serviceUrl + endpoint)
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
