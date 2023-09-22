package mavmi.telegram_bot.rocket_bot.httpHandler;

import com.google.gson.JsonObject;
import mavmi.telegram_bot.rocket_bot.jsonHandler.handler.JsonHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.*;
import okhttp3.*;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpHandler {
    private static final int COUNT = 100;
    private static final int MAX_HISTORY_COUNT = 25;

    private final JsonHandler jsonHandler;
    private final String loginUrl;
    private final String meUrl;
    private final String imListUrl;
    private final String imHistoryUrl;
    private final String groupsListUrl;
    private final String groupsHistoryUrl;
    private final String rcUidHeader;
    private final String rcTokenHeader;
    private final String hostUrl;

    public HttpHandler(
            JsonHandler jsonHandler,
            String loginUrl,
            String meUrl,
            String imListUrl,
            String imHistoryUrl,
            String groupsListUrl,
            String groupsHistoryUrl,
            String rcUidHeader,
            String rcTokenHeader,
            String hostUrl
    ) {
        this.jsonHandler = jsonHandler;
        this.loginUrl = loginUrl;
        this.meUrl = meUrl;
        this.imListUrl = imListUrl;
        this.imHistoryUrl = imHistoryUrl;
        this.groupsListUrl = groupsListUrl;
        this.groupsHistoryUrl = groupsHistoryUrl;
        this.rcUidHeader = rcUidHeader;
        this.rcTokenHeader = rcTokenHeader;
        this.hostUrl = hostUrl;
    }

    @Nullable
    public LoginJsonModel auth(String username, String passwd) {
        JsonObject loginJsonObject = jsonHandler.createLoginRequest(username, passwd);
        String responseString = sendRequest(
                loginUrl,
                null,
                null,
                loginJsonObject
        );
        if (responseString == null) {
            return null;
        }
        return jsonHandler.parseLoginResponse(responseString);
    }

    @Nullable
    public MeJsonModel me(String rcUid, String rcToken) {
        String responseString = sendRequest(
                meUrl,
                null,
                Map.of(rcUidHeader, rcUid, rcTokenHeader, rcToken, "Host", hostUrl),
                null
        );
        if (responseString == null) {
            return null;
        }
        return jsonHandler.parseMeResponse(responseString);
    }

    @Nullable
    public List<ImListJsonModel> imList(String rcUid, String rcToken, Map<String, String> chatIdToLastKnowMsgId) {
        List<ImListJsonModel> allImListJsonModels = new ArrayList<>();

        int offset = 0;
        while (true) {
            String responseString = sendRequest(
                    imListUrl,
                    Map.of("count", Integer.toString(COUNT), "offset", Integer.toString(offset)),
                    Map.of(rcUidHeader, rcUid, rcTokenHeader, rcToken, "Host", hostUrl),
                    null
            );
            if (responseString == null) {
                return null;
            }
            offset += COUNT;

            List<ImListJsonModel> iterImListJsonModels = jsonHandler.parseImListResponse(responseString);
            if (iterImListJsonModels.isEmpty()) {
                return allImListJsonModels;
            }
            for (ImListJsonModel iterImListJsonModel : iterImListJsonModels) {
                iterImListJsonModel.setHistoryResponses(
                        imHistory(
                                rcUid,
                                rcToken,
                                iterImListJsonModel.getChatId(),
                                chatIdToLastKnowMsgId.get(iterImListJsonModel.getChatId())
                        )
                );
                allImListJsonModels.add(iterImListJsonModel);
            }
        }
    }

    @Nullable
    public List<ImHistoryJsonModel> imHistory(String rcUid, String rcToken, String roomId, @Nullable String lastKnownMsgId) {
        List<ImHistoryJsonModel> allImHistoryJsonModels = new ArrayList<>();

        int offset = 0;
        while (true) {
            String responseString = sendRequest(
                    imHistoryUrl,
                    Map.of("count", Integer.toString(COUNT), "offset", Integer.toString(offset), "roomId", roomId),
                    Map.of(rcUidHeader, rcUid, rcTokenHeader, rcToken, "Host", hostUrl),
                    null
            );
            if (responseString == null) {
                return null;
            }
            offset += COUNT;

            List<ImHistoryJsonModel> iterImHistoryJsonModels = jsonHandler.parseImHistoryResponse(responseString);
            if (iterImHistoryJsonModels.isEmpty() || allImHistoryJsonModels.size() >= MAX_HISTORY_COUNT) {
                return allImHistoryJsonModels;
            }
            for (ImHistoryJsonModel iterImHistoryJsonModel : iterImHistoryJsonModels) {
                if (iterImHistoryJsonModel.getMessage().getAuthor().getId().equals(rcUid) ||
                        iterImHistoryJsonModel.getMessage().getId().equals(lastKnownMsgId)) {
                    return allImHistoryJsonModels;
                } else {
                    allImHistoryJsonModels.add(0, iterImHistoryJsonModel);
                }
            }
        }
    }

    @Nullable
    public List<GroupsListJsonModel> groupsList(String rcUid, String rcToken, Map<String, String> groupIdToLastKnowMentionMsgId) {
        List<GroupsListJsonModel> allGroupsListJsonModels = new ArrayList<>();

        int offset = 0;
        while (true) {
            String responseString = sendRequest(
                    groupsListUrl,
                    Map.of("count", Integer.toString(COUNT), "offset", Integer.toString(offset)),
                    Map.of(rcUidHeader, rcUid, rcTokenHeader, rcToken, "Host", hostUrl),
                    null
            );
            if (responseString == null) {
                return null;
            }
            offset += COUNT;

            List<GroupsListJsonModel> iterGroupsListJsonModels = jsonHandler.parseGroupsListResponse(responseString);
            if (iterGroupsListJsonModels.isEmpty()) {
                return allGroupsListJsonModels;
            }
            for (GroupsListJsonModel iterGroupsListJsonModel : iterGroupsListJsonModels) {
                iterGroupsListJsonModel.setHistoryResponses(
                        groupsHistory(
                                rcUid,
                                rcToken,
                                iterGroupsListJsonModel.getGroupId(),
                                groupIdToLastKnowMentionMsgId.get(iterGroupsListJsonModel.getGroupId())
                        )
                );
                allGroupsListJsonModels.add(iterGroupsListJsonModel);
            }
        }
    }

    @Nullable
    public List<GroupsHistoryJsonModel> groupsHistory(String rcUid, String rcToken, String roomId, @Nullable String lastKnownMentionMsgId) {
        List<GroupsHistoryJsonModel> allGroupsHistoryJsonModels = new ArrayList<>();

        int offset = 0;
        while (true) {
            String responseString = sendRequest(
                    groupsHistoryUrl,
                    Map.of("count", Integer.toString(COUNT), "offset", Integer.toString(offset), "roomId", roomId),
                    Map.of(rcUidHeader, rcUid, rcTokenHeader, rcToken, "Host", hostUrl),
                    null
            );
            if (responseString == null) {
                return null;
            }
            offset += COUNT;

            List<GroupsHistoryJsonModel> iterGroupsHistoryJsonModels = jsonHandler.parseGroupsHistoryResponse(responseString);
            if (iterGroupsHistoryJsonModels.isEmpty() || allGroupsHistoryJsonModels.size() >= MAX_HISTORY_COUNT) {
                return allGroupsHistoryJsonModels;
            }
            for (GroupsHistoryJsonModel iterGroupsHistoryJsonModel : iterGroupsHistoryJsonModels) {
                if (iterGroupsHistoryJsonModel.getMessage().getAuthor().getId().equals(rcUid) ||
                        iterGroupsHistoryJsonModel.getMessage().getId().equals(lastKnownMentionMsgId)) {
                    return allGroupsHistoryJsonModels;
                } else if (iterGroupsHistoryJsonModel.getMentionsIdx().contains(rcUid) || iterGroupsHistoryJsonModel.getMentionsIdx().contains("all")) {
                    allGroupsHistoryJsonModels.add(0, iterGroupsHistoryJsonModel);
                }
            }
        }
    }

    @Nullable
    private String sendRequest(String url, Map<String, String> params, Map<String, String> headers, JsonObject requestJsonObject) {
        OkHttpClient okHttp = new OkHttpClient();

        HttpUrl httpUrl = HttpUrl.parse(url);
        if (httpUrl == null) {
            return null;
        }

        HttpUrl.Builder httpUrlBuilder = httpUrl.newBuilder();
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpUrlBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }

        @SuppressWarnings("KotlinInternalInJava")
        Request.Builder requestBuilder = new Request.Builder().url(httpUrlBuilder.build());
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                requestBuilder.addHeader(header.getKey(), header.getValue());
            }
        }

        if (requestJsonObject != null) {
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestJsonObject.toString()
            );
            requestBuilder.post(requestBody);
        }

        int maxAttempts = 3;
        for (int i = 0; i < maxAttempts; i++) {
            try {
                Request request = requestBuilder.build();
                Response response = okHttp.newCall(request).execute();
                ResponseBody body = response.body();
                if (body != null) {
                    return body.string();
                }
            } catch (IOException ignored) {

            }
        }
        return null;
    }
}
