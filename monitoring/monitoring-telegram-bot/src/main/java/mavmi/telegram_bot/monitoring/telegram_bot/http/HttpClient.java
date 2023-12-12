package mavmi.telegram_bot.monitoring.telegram_bot.http;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.BotTaskManagerJson;
import mavmi.telegram_bot.common.utils.http.AbsHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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
                serviceUrl,
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
}
