package mavmi.telegram_bot.rocketchat.websocketClient;

import lombok.Getter;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class RocketWebsocketClientBuilder {

    private final RemoteParameterPlugin parameterPlugin;
    private final String url;

    public RocketWebsocketClientBuilder(
        RemoteParameterPlugin parameterPlugin,
        @Value("${websocket.client.url}") String url
    ) {
        this.parameterPlugin = parameterPlugin;
        this.url = url;
    }

    public RocketWebsocketClient getWebsocketClient() {
        return new RocketWebsocketClient(url, this.getConnectionTimeout(), this.getAwaitingPeriodMillis());
    }

    public long getConnectionTimeout() {
        return parameterPlugin.getParameter("rocket.websocket.client.timeout-sec").getLong();
    }

    public long getAwaitingPeriodMillis() {
        return parameterPlugin.getParameter("rocket.websocket.client.awaiting-period-millis").getLong();
    }
}
