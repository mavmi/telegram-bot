package mavmi.telegram_bot.water_stuff.telegram_bot.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.UserJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.UserMessageJson;
import mavmi.telegram_bot.common.utils.http.AbsHttpClient;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.HttpURLConnection;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient {

    public final String serviceUrl;
    public final String serviceProcessRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.processRequest}") String serviceProcessRequestEndpoint
    ){
        this.serviceUrl = serviceUrl;
        this.serviceProcessRequestEndpoint = serviceProcessRequestEndpoint;
    }

    public int processRequest(Message telegramMessage) {
        User telegramUser = telegramMessage.from();

        UserJson userJson = UserJson
                .builder()
                .id(telegramUser.id())
                .username(telegramUser.username())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .build();

        UserMessageJson userMessageJson = UserMessageJson
                .builder()
                .textMessage(telegramMessage.text())
                .build();

        return sendRequest(
                serviceProcessRequestEndpoint,
                BotRequestJson
                        .builder()
                        .chatId(telegramMessage.chat().id())
                        .userJson(userJson)
                        .userMessageJson(userMessageJson)
                        .build()
        );
    }

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
