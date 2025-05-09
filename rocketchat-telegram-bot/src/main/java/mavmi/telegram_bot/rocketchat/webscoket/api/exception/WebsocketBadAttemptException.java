package mavmi.telegram_bot.rocketchat.webscoket.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WebsocketBadAttemptException extends RuntimeException {

    public WebsocketBadAttemptException(String msg) {
        super(msg);
    }

    public WebsocketBadAttemptException(Exception e) {
        super(e);
    }
}
