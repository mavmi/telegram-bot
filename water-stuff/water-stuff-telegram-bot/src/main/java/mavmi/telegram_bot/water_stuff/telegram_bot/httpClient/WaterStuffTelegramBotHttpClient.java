package mavmi.telegram_bot.water_stuff.telegram_bot.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.CallbackQueryJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.common.UserJson;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.ReminderServiceRs;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.httpClient.HttpClient;
import mavmi.telegram_bot.common.httpFilter.userSession.UserSessionHttpFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class WaterStuffTelegramBotHttpClient extends HttpClient {

    public final String serviceUrl;
    public final String waterStuffServiceRequestEndpoint;
    public final String reminderServiceRequestEndpoint;

    public WaterStuffTelegramBotHttpClient(
            SslBundles sslBundles,
            RestTemplateBuilder restTemplateBuilder,
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.waterStuffServiceRequest}") String waterStuffServiceRequestEndpoint,
            @Value("${service.endpoint.reminderServiceRequest}") String reminderServiceRequestEndpoint
    ){
        super(sslBundles.getBundle("telegram-bot"), restTemplateBuilder);
        this.serviceUrl = serviceUrl;
        this.waterStuffServiceRequestEndpoint = waterStuffServiceRequestEndpoint;
        this.reminderServiceRequestEndpoint = reminderServiceRequestEndpoint;
    }

    @Nullable
    @SneakyThrows
    public ResponseEntity<WaterStuffServiceRs> waterStuffServiceRequest(CallbackQuery callbackQuery) {
        long chatId = callbackQuery.from().id();
        ObjectMapper objectMapper = new ObjectMapper();

        CallbackQueryJson callbackQueryJson = CallbackQueryJson
                .builder()
                .data(callbackQuery.data())
                .messageId(callbackQuery.maybeInaccessibleMessage().messageId())
                .build();

        WaterStuffServiceRq waterStuffServiceRq = WaterStuffServiceRq
                .builder()
                .chatId(chatId)
                .callbackQueryJson(callbackQueryJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(waterStuffServiceRq);

        return sendPostRequest(
                serviceUrl,
                waterStuffServiceRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, Long.toString(chatId)),
                requestBody,
                WaterStuffServiceRs.class
        );
    }

    @Nullable
    @SneakyThrows
    public ResponseEntity<WaterStuffServiceRs> waterStuffServiceRequest(Message telegramMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        User telegramUser = telegramMessage.from();

        UserJson userJson = UserJson
                .builder()
                .id(telegramUser.id())
                .username(telegramUser.username())
                .firstName(telegramUser.firstName())
                .lastName(telegramUser.lastName())
                .build();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(telegramMessage.text())
                .build();

        WaterStuffServiceRq waterStuffServiceRq = WaterStuffServiceRq
                .builder()
                .chatId(telegramMessage.chat().id())
                .userJson(userJson)
                .messageJson(messageJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(waterStuffServiceRq);

        return sendPostRequest(
                serviceUrl,
                waterStuffServiceRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, Long.toString(telegramUser.id())),
                requestBody,
                WaterStuffServiceRs.class
        );
    }

    @Nullable
    public ResponseEntity<ReminderServiceRs> reminderServiceRequest() {
        return sendGetRequest(serviceUrl, reminderServiceRequestEndpoint, ReminderServiceRs.class);
    }
}
