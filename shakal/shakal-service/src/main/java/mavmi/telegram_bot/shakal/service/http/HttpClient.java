package mavmi.telegram_bot.shakal.service.http;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceKeyboardJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.http.AbsHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient<ServiceRequestJson> {

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

    public int sendText(
            long chatId,
            String text
    ) {
        return sendRequest(
                telegramBotUrl,
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

    public int sendDice(
            long chatId,
            String msg,
            String[] buttons
    ) {
        return sendRequest(
                telegramBotUrl,
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
}
