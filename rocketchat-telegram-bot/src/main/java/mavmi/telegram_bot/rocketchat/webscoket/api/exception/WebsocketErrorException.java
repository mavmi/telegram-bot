package mavmi.telegram_bot.rocketchat.webscoket.api.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class WebsocketErrorException extends RuntimeException {

    public WebsocketErrorException(String msg) {
        super(msg);
    }

    public WebsocketErrorException(Exception e) {
        super(e);
    }
}
