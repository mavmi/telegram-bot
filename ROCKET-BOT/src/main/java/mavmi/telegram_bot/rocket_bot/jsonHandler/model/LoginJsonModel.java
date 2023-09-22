package mavmi.telegram_bot.rocket_bot.jsonHandler.model;

import com.google.gson.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class LoginJsonModel {
    private Boolean success;
    private JsonObject message;
}
