package mavmi.telegram_bot.rocket_bot.jsonHandler.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.LoginResponse;

public class JsonHandler {
    public JsonObject createLoginRequest(String username, String passwd) {
        JsonObject tmpJsonObject = new JsonObject();

        tmpJsonObject.addProperty("msg", "method");
        tmpJsonObject.addProperty("id", "1");
        tmpJsonObject.addProperty("method", "login");

        JsonObject param = new JsonObject();
        param.addProperty("ldap", true);
        param.addProperty("username", username);
        param.addProperty("ldapPass", passwd);
        param.add("ldapOptions", new JsonObject());

        JsonArray params = new JsonArray();
        params.add(param);
        tmpJsonObject.add("params", params);

        JsonObject loginRequestJsonObject = new JsonObject();
        loginRequestJsonObject.addProperty("message", tmpJsonObject.toString());

        return loginRequestJsonObject;
    }

    public LoginResponse parseLoginResponse(String msg) {
        JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();

        Boolean success = jsonObject.get("success").getAsBoolean();
        JsonObject message = JsonParser.parseString(jsonObject.get("message").getAsString()).getAsJsonObject();

        return LoginResponse.builder()
                .success(success)
                .message(message)
                .build();
    }
}
