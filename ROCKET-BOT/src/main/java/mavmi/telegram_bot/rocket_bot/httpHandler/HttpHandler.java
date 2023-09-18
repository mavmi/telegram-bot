package mavmi.telegram_bot.rocket_bot.httpHandler;

import com.google.gson.JsonObject;
import mavmi.telegram_bot.rocket_bot.jsonHandler.handler.JsonHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.ImHistoryResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.ImListResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.LoginResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.MeResponse;
import okhttp3.*;
import org.springframework.lang.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class HttpHandler {
    private static final int MAX_COUNT = 100000;
    private static final int COUNT = 5;

    private final JsonHandler jsonHandler;
    private final String loginUrl;
    private final String meUrl;
    private final String imListUrl;
    private final String imHistoryUrl;
    private final String rcUidHeader;
    private final String rcTokenHeader;
    private final String hostUrl;

    public HttpHandler(
            JsonHandler jsonHandler,
            String loginUrl,
            String meUrl,
            String imListUrl,
            String imHistoryUrl,
            String rcUidHeader,
            String rcTokenHeader,
            String hostUrl
    ) {
        this.jsonHandler = jsonHandler;
        this.loginUrl = loginUrl;
        this.meUrl = meUrl;
        this.imListUrl = imListUrl;
        this.imHistoryUrl = imHistoryUrl;
        this.rcUidHeader = rcUidHeader;
        this.rcTokenHeader = rcTokenHeader;
        this.hostUrl = hostUrl;
    }

    @Nullable
    public LoginResponse auth(String username, String passwd) {
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
    public MeResponse me(String rcUid, String rcToken) {
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
    public List<ImListResponse> imList(String rcUid, String rcToken, Map<String, String> chatIdToLastKnowMsgId) {
        List<ImListResponse> allListResponses = new ArrayList<>();

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

            List<ImListResponse> tmpListResponses = jsonHandler.parseImListResponse(rcUid, responseString);
            if (tmpListResponses.isEmpty()) {
                return allListResponses;
            }
            for (ImListResponse tmpListResponse : tmpListResponses) {
                tmpListResponse.setHistoryResponses(
                        imHistory(
                                rcUid,
                                rcToken,
                                tmpListResponse.getChat_id(),
                                chatIdToLastKnowMsgId.get(tmpListResponse.getChat_id())
                        )
                );
                allListResponses.add(tmpListResponse);
            }
        }
    }

    @Nullable
    public List<ImHistoryResponse> imHistory(String rcUid, String rcToken, String roomId, @Nullable String lastKnownMsgId) {
        List<ImHistoryResponse> allHistoryResponses = new ArrayList<>();

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

            List<ImHistoryResponse> tmpHistoryResponses = jsonHandler.parseImHistoryResponse(responseString);
            if (tmpHistoryResponses.isEmpty()) {
                return allHistoryResponses;
            }
            for (ImHistoryResponse tmpHistoryResponse : tmpHistoryResponses) {
                if (tmpHistoryResponse.getAuthor_id().equals(rcUid) || tmpHistoryResponse.getMsg_id().equals(lastKnownMsgId)) {
                    return allHistoryResponses;
                } else {
                    allHistoryResponses.add(0, tmpHistoryResponse);
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
