package mavmi.telegram_bot.rocketchat.old.websocket.api.messageHandler;

import mavmi.telegram_bot.lib.dto.service.service.ServiceRequest;

@FunctionalInterface
public interface OnResult<RequestType extends ServiceRequest> {
    void process(RequestType request, Object... payload);
}
