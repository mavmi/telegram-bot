package mavmi.telegram_bot.shakal.telegram_bot.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Dice;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.DiceJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.UserJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.UserMessageJson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Slf4j
@Component
public class HttpClient {
    private final ObjectMapper objectMapper;
    private final OkHttpClient httpClient;

    public final String serviceUrl;
    public final String serviceProcessRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.processRequest}") String serviceProcessRequestEndpoint
    ) {
        this.objectMapper = new ObjectMapper();
        this.httpClient = new OkHttpClient();
        this.serviceUrl = serviceUrl;
        this.serviceProcessRequestEndpoint = serviceProcessRequestEndpoint;
    }

    public void processRequest(
            Message telegramMessage,
            @Nullable User telegramUser,
            @Nullable Dice telegramDice
    ) {


        UserMessageJson userMessageJson = UserMessageJson
                .builder()
                .textMessage(telegramMessage.text())
                .date(new Date())
                .build();

        UserJson userJson = null;
        if (telegramUser != null) {
            userJson = UserJson
                    .builder()
                    .id(telegramUser.id())
                    .username(telegramUser.username())
                    .firstName(telegramUser.firstName())
                    .lastName(telegramUser.lastName())
                    .build();
        }

        DiceJson diceJson = null;
        if (telegramDice != null) {
            diceJson = DiceJson
                    .builder()
                    .userDiceValue(telegramDice.value())
                    .build();
        }

        sendRequest(
                serviceProcessRequestEndpoint,
                BotRequestJson
                        .builder()
                        .chatId(telegramMessage.chat().id())
                        .userJson(userJson)
                        .userMessageJson(userMessageJson)
                        .diceJson(diceJson)
                        .build()
        );
    }

    public void sendRequest(
            String endpoint,
            BotRequestJson botRequestJson
    ) {
        try {
            String requestBodyStr = objectMapper.writeValueAsString(botRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request request = new Request.Builder()
                    .url(serviceUrl + endpoint)
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
