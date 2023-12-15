package mavmi.telegram_bot.shakal.telegram_bot.http;

import com.pengrad.telegrambot.model.Dice;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.DiceJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.UserJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.UserMessageJson;
import mavmi.telegram_bot.common.http.AbsHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient<BotRequestJson> {

    public final String serviceUrl;
    public final String serviceProcessRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.processRequest}") String serviceProcessRequestEndpoint
    ) {
        this.serviceUrl = serviceUrl;
        this.serviceProcessRequestEndpoint = serviceProcessRequestEndpoint;
    }

    public int processRequest(
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

        return sendRequest(
                serviceUrl,
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
}
