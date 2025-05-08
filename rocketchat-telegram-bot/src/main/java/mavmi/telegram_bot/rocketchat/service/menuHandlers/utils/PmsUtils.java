package mavmi.telegram_bot.rocketchat.service.menuHandlers.utils;

import lombok.RequiredArgsConstructor;
import mavmi.parameters_management_system.client.plugin.impl.remote.RemoteParameterPlugin;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PmsUtils {

    private final RemoteParameterPlugin parameterPlugin;

    public long getConnectionTimeout() {
        return parameterPlugin.getParameter("rocket.websocket.client.timeout-sec").getLong();
    }

    public long getAwaitingPeriodMillis() {
        return parameterPlugin.getParameter("rocket.websocket.client.awaiting-period-millis").getLong();
    }

    public long getDeleteAfterMillisNotification() {
        return parameterPlugin.getParameter("rocket.service.delete-after-millis.notification").getLong();
    }

    public long getDeleteAfterMillisQr() {
        return parameterPlugin.getParameter("rocket.service.delete-after-millis.qr").getLong();
    }
}
