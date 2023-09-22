package mavmi.telegram_bot.rocket_bot.jsonHandler.handler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public LoginJsonModel parseLoginResponse(String msg) {
        JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();

        Boolean success = jsonObject.get("success").getAsBoolean();
        JsonObject message = JsonParser.parseString(jsonObject.get("message").getAsString()).getAsJsonObject();

        return LoginJsonModel.builder()
                .success(success)
                .message(message)
                .build();
    }

    public MeJsonModel parseMeResponse(String msg) {
        JsonObject jsonObject = JsonParser.parseString(msg).getAsJsonObject();

        return MeJsonModel.builder()
                .id(jsonObject.get("_id").getAsString())
                .username(jsonObject.get("username").getAsString())
                .email(jsonObject.get("email").getAsString())
                .name(jsonObject.get("name").getAsString())
                .statusText(jsonObject.get("statusText").getAsString())
                .statusConnection(jsonObject.get("statusConnection").getAsString())
                .build();
    }

    public List<ImListJsonModel> parseImListResponse(String msg) {
        List<ImListJsonModel> imListJsonModels = new ArrayList<>();

        JsonArray imsJsonArray = JsonParser
                .parseString(msg)
                .getAsJsonObject()
                .get("ims")
                .getAsJsonArray();

        for (JsonElement imJsonElement : imsJsonArray) {
            JsonObject imJsonObject = imJsonElement.getAsJsonObject();
            if (!imJsonObject.has("lastMessage")) {
                continue;
            }

            imListJsonModels.add(
                    ImListJsonModel.builder()
                            .chatId(imJsonObject.get("_id").getAsString())
                            .lastMessage(parseMessage(imJsonObject.get("lastMessage").getAsJsonObject().toString()))
                            .historyResponses(null)
                            .build()
            );
        }

        return imListJsonModels;
    }

    public List<ImHistoryJsonModel> parseImHistoryResponse(String msg) {
        List<ImHistoryJsonModel> responseList = new ArrayList<>();

        JsonArray messagesJsonArray = JsonParser
                .parseString(msg)
                .getAsJsonObject()
                .get("messages")
                .getAsJsonArray();

        for (JsonElement messageJsonElement : messagesJsonArray) {
            JsonObject messageJsonObject = messageJsonElement.getAsJsonObject();

            responseList.add(
                    ImHistoryJsonModel.builder()
                            .message(parseMessage(messageJsonObject.toString()))
                            .build()
            );
        }

        return responseList;
    }

    public List<GroupsListJsonModel> parseGroupsListResponse(String msg) {
        List<GroupsListJsonModel> responseList = new ArrayList<>();

        JsonArray groupsJsonArray = JsonParser
                .parseString(msg)
                .getAsJsonObject()
                .get("groups")
                .getAsJsonArray();

        for (JsonElement groupJsonElement : groupsJsonArray) {
            JsonObject groupJsonObject = groupJsonElement.getAsJsonObject();
            if (!groupJsonObject.has("lastMessage")) {
                continue;
            }

            responseList.add(
                    GroupsListJsonModel.builder()
                            .groupId(groupJsonObject.get("_id").getAsString())
                            .groupName(groupJsonObject.get("name").getAsString())
                            .lastMessage(parseMessage(groupJsonObject.get("lastMessage").getAsJsonObject().toString()))
                            .historyResponses(null)
                            .build()
            );
        }

        return responseList;
    }

    public List<GroupsHistoryJsonModel> parseGroupsHistoryResponse(String msg) {
        List<GroupsHistoryJsonModel> historyResponses = new ArrayList<>();

        JsonArray messagesJsonArray = JsonParser
                .parseString(msg)
                .getAsJsonObject()
                .get("messages")
                .getAsJsonArray();

        for (JsonElement messageJsonElement : messagesJsonArray) {
            JsonObject messageJsonObject = messageJsonElement.getAsJsonObject();
            if (!messageJsonObject.has("mentions")) {
                continue;
            }
            JsonArray mentions = messageJsonObject.get("mentions").getAsJsonArray();
            Set<String> mentionsIdx = new HashSet<>();

            for (JsonElement mentionJsonElement : mentions) {
                mentionsIdx.add(mentionJsonElement.getAsJsonObject().get("_id").getAsString());
            }

            historyResponses.add(
                    GroupsHistoryJsonModel.builder()
                            .message(parseMessage(messageJsonObject.toString()))
                            .mentionsIdx(mentionsIdx)
                            .build()
            );
        }

        return historyResponses;
    }

    public UserJsonModel parseUser(String msg) {
        JsonObject userJsonObject = JsonParser.parseString(msg).getAsJsonObject();

        return UserJsonModel.builder()
                .id(userJsonObject.get("_id").getAsString())
                .name(userJsonObject.get("name").getAsString())
                .build();
    }

    @SneakyThrows
    public MessageJsonModel parseMessage(String msg) {
        JsonObject messageJsonObject = JsonParser.parseString(msg).getAsJsonObject();

        return MessageJsonModel.builder()
                .id(messageJsonObject.get("_id").getAsString())
                .text(messageJsonObject.get("msg").getAsString())
                .timestamp(CommonUtils.OUTPUT_DATE_FORMAT.format(CommonUtils.INPUT_DATE_FORMAT.parse(messageJsonObject.get("ts").getAsString())))
                .author(parseUser(messageJsonObject.get("u").getAsJsonObject().toString()))
                .build();
    }
}
