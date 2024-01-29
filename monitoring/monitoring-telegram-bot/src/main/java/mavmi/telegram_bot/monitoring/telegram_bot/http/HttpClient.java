package mavmi.telegram_bot.monitoring.telegram_bot.http;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.User;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.UserJson;
import mavmi.telegram_bot.common.dto.json.bot.inner.UserMessageJson;
import mavmi.telegram_bot.common.http.AbsHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HttpClient extends AbsHttpClient<BotRequestJson> {

    public final String serviceUrl;
    public final String processRequestEndpoint;

    public HttpClient(
            @Value("${service.url}") String serviceUrl,
            @Value("${service.endpoint.processRequest}") String processRequestEndpoint
    ) {
        this.serviceUrl = serviceUrl;
        this.processRequestEndpoint = processRequestEndpoint;
    }

    public int processRequest(Message telegramMessage, String target) {
        User telegramUser = telegramMessage.from();

        return sendRequest(
                serviceUrl,
                processRequestEndpoint,
                BotRequestJson
                        .builder()
                        .chatId(telegramMessage.chat().id())
                        .userJson(
                                UserJson
                                        .builder()
                                        .id(telegramUser.id())
                                        .username(telegramUser.username())
                                        .firstName(telegramUser.firstName())
                                        .lastName(telegramUser.lastName())
                                        .build()
                        )
                        .userMessageJson(
                                UserMessageJson
                                        .builder()
                                        .textMessage(telegramMessage.text())
                                        .build()
                        )
                        .botTaskManagerJson(
                                BotTaskManagerJson
                                        .builder()
                                        .target(target)
                                        .message(telegramMessage.text())
                                        .build()
                        )
                        .build()
        );
    }
}
