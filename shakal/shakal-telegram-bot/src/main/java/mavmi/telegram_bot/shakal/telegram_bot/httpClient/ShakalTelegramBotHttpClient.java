package mavmi.telegram_bot.shakal.telegram_bot.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.Dice;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.httpClient.HttpClient;
import mavmi.telegram_bot.common.httpFilter.userSession.UserSessionHttpFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Slf4j
@Component
public class ShakalTelegramBotHttpClient extends HttpClient {

    public final String serviceUrl;
    public final String shakaServiceRequestEndpoint;

    public ShakalTelegramBotHttpClient(
            SslBundles sslBundles,
            RestTemplateBuilder restTemplateBuilder,
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.shakalServiceRequest}") String shakaServiceRequestEndpoint
    ) {
        super(sslBundles.getBundle("telegram-bot"), restTemplateBuilder);
        this.serviceUrl = serviceUrl;
        this.shakaServiceRequestEndpoint = shakaServiceRequestEndpoint;
    }

    @Nullable
    @SneakyThrows
    public ResponseEntity<ShakalServiceRs> shakalServiceRequest(
            Message telegramMessage,
            @Nullable User telegramUser,
            @Nullable Dice telegramDice
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(telegramMessage.text())
                .date(new Date(telegramMessage.date().longValue()))
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

        ShakalServiceRq shakalServiceRq = ShakalServiceRq
                .builder()
                .chatId(telegramMessage.chat().id())
                .userJson(userJson)
                .messageJson(messageJson)
                .diceJson(diceJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(shakalServiceRq);

        return sendPostRequest(
                serviceUrl,
                shakaServiceRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, Long.toString(telegramUser.id())),
                requestBody,
                ShakalServiceRs.class
        );
    }
}
