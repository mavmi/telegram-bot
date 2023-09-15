package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {
    private Boolean success;
    private JsonObject message;
}
