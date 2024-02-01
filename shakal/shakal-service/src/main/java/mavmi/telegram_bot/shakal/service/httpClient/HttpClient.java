package mavmi.telegram_bot.shakal.service.httpClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.impl.shakal.telegram_bot.ShakalTelegramBotRq;
import mavmi.telegram_bot.common.httpClient.AbstractHttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpClient extends AbstractHttpClient {

    public final String telegramBotUrl;
    public final String telegramBotSendTextEndpoint;
    public final String telegramBotSendKeyboardEndpoint;
    public final String telegramBotSendDiceEndpoint;

    public HttpClient(
            @Value("${telegram-bot.url}") String telegramBotUrl,
            @Value("${telegram-bot.endpoint.sendText}") String telegramBotSendTextEndpoint,
            @Value("${telegram-bot.endpoint.sendKeyboard}") String telegramBotSendKeyboardEndpoint,
            @Value("${telegram-bot.endpoint.sendDice}") String telegramBotSendDiceEndpoint
    ) {
        this.telegramBotUrl = telegramBotUrl;
        this.telegramBotSendTextEndpoint = telegramBotSendTextEndpoint;
        this.telegramBotSendKeyboardEndpoint = telegramBotSendKeyboardEndpoint;
        this.telegramBotSendDiceEndpoint = telegramBotSendDiceEndpoint;
    }

    @SneakyThrows
    public Response sendText(
            long chatId,
            String text
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(text)
                .build();

        ShakalTelegramBotRq shakalTelegramBotRq = ShakalTelegramBotRq
                .builder()
                .chatId(chatId)
                .messageJson(messageJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(shakalTelegramBotRq);

        return sendRequest(
                telegramBotUrl,
                telegramBotSendTextEndpoint,
                requestBody
        );
    }

    @SneakyThrows
    public Response sendDice(
            long chatId,
            String msg,
            String[] buttons
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(buttons)
                .build();

        ShakalTelegramBotRq shakalTelegramBotRq = ShakalTelegramBotRq
                .builder()
                .chatId(chatId)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(shakalTelegramBotRq);

        return sendRequest(
                telegramBotUrl,
                telegramBotSendDiceEndpoint,
                requestBody
        );
    }

    @SneakyThrows
    public Response sendKeyboard(
            long chatId,
            String msg,
            String[] buttons
    ) {
        ObjectMapper objectMapper = new ObjectMapper();

        MessageJson messageJson = MessageJson
                .builder()
                .textMessage(msg)
                .build();

        KeyboardJson keyboardJson = KeyboardJson
                .builder()
                .keyboardButtons(buttons)
                .build();

        ShakalTelegramBotRq shakalTelegramBotRq = ShakalTelegramBotRq
                .builder()
                .chatId(chatId)
                .messageJson(messageJson)
                .keyboardJson(keyboardJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(shakalTelegramBotRq);

        return sendRequest(
                telegramBotUrl,
                telegramBotSendKeyboardEndpoint,
                requestBody
        );
    }
}
