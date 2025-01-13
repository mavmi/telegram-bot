package mavmi.telegram_bot.rocketchat.websocket.api.messageHandler;

import mavmi.telegram_bot.common.service.dto.service.ServiceRequest;

@FunctionalInterface
public interface OnResult<RequestType extends ServiceRequest> {
    void process(RequestType request, Object... payload);
}
