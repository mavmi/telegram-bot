package mavmi.telegram_bot.rocketchat.websocketClient;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RocketchatWebsocketClientBuilder {

    private final String url;
    private final long connectionTimeout;
    private final long awaitingPeriodMillis;

    public RocketchatWebsocketClientBuilder(
        @Value("${websocket.client.url}") String url,
        @Value("${websocket.client.timeout-sec}") long connectionTimeout,
        @Value("${websocket.client.awaiting-period-millis}") long awaitingPeriodMillis
    ) {
        this.url = url;
        this.connectionTimeout = connectionTimeout;
        this.awaitingPeriodMillis = awaitingPeriodMillis;
    }

    public RocketchatWebsocketClient getWebsocketClient() {
        return new RocketchatWebsocketClient(url, connectionTimeout, awaitingPeriodMillis);
    }
}
