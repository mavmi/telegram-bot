package mavmi.telegram_bot.rocket_bot.jsonHandler.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.ImHistoryResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.ImListResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.LoginResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.MeResponse;

import java.util.ArrayList;
import java.util.List;

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

    public MeResponse parseMeResponse(String msg) {
        JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();

        return MeResponse.builder()
                .id(jsonObject.get("_id").getAsString())
                .username(jsonObject.get("username").getAsString())
                .email(jsonObject.get("email").getAsString())
                .name(jsonObject.get("name").getAsString())
                .statusText(jsonObject.get("statusText").getAsString())
                .statusConnection(jsonObject.get("statusConnection").getAsString())
                .build();
    }

    public List<ImListResponse> parseImListResponse(String rcUid, String msg) {
        List<ImListResponse> responseList = new ArrayList<>();

        JsonArray jsonArray = JsonParser
                .parseString(msg)
                .getAsJsonObject()
                .get("ims")
                .getAsJsonArray();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (!jsonObject.has("lastMessage")) {
                continue;
            }

            JsonObject lastMessageJsonObject = jsonObject.get("lastMessage").getAsJsonObject();

            ImListResponse imListResponse = ImListResponse.builder()
                    .rc_uid(rcUid)
                    .chat_id(jsonObject.get("_id").getAsString())
                    .last_msg_id(lastMessageJsonObject.getAsJsonObject().get("_id").getAsString())
                    .last_msg_author_id(lastMessageJsonObject.getAsJsonObject().get("u").getAsJsonObject().get("_id").getAsString())
                    .last_msg_author_name(lastMessageJsonObject.getAsJsonObject().get("u").getAsJsonObject().get("name").getAsString())
                    .historyResponses(null)
                    .build();

            responseList.add(imListResponse);
        }

        return responseList;
    }

    @SneakyThrows
    public List<ImHistoryResponse> parseImHistoryResponse(String msg) {
        List<ImHistoryResponse> responseList = new ArrayList<>();

        JsonArray jsonArray = JsonParser
                .parseString(msg)
                .getAsJsonObject()
                .get("messages")
                .getAsJsonArray();

        for (JsonElement jsonElement : jsonArray) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            JsonObject author = jsonObject.get("u").getAsJsonObject();

            ImHistoryResponse imHistoryResponse = ImHistoryResponse.builder()
                    .msg_id(jsonObject.get("_id").getAsString())
                    .msg(jsonObject.get("msg").getAsString())
                    .timestamp(ImHistoryResponse.OUTPUT_DATE_FORMAT.format(ImHistoryResponse.INPUT_DATE_FORMAT.parse(jsonObject.get("ts").getAsString())))
                    .author_id(author.get("_id").getAsString())
                    .author_name(author.get("name").getAsString())
                    .build();

            responseList.add(imHistoryResponse);
        }

        return responseList;
    }
}
