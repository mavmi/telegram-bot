package mavmi.telegram_bot.rocket_bot.httpHandler;

import com.google.gson.JsonObject;
import mavmi.telegram_bot.rocket_bot.jsonHandler.handler.JsonHandler;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.LoginResponse;
import mavmi.telegram_bot.rocket_bot.jsonHandler.model.MeResponse;
import okhttp3.*;

import java.io.IOException;

public class HttpHandler {
    private final JsonHandler jsonHandler;
    private final String loginUrl;
    private final String meUrl;
    private final String rcUidHeader;
    private final String rcTokenHeader;
    private final String hostUrl;

    public HttpHandler(JsonHandler jsonHandler, String loginUrl, String meUrl, String rcUidHeader, String rcTokenHeader, String hostUrl) {
        this.jsonHandler = jsonHandler;
        this.loginUrl = loginUrl;
        this.meUrl = meUrl;
        this.rcUidHeader = rcUidHeader;
        this.rcTokenHeader = rcTokenHeader;
        this.hostUrl = hostUrl;
    }

    public LoginResponse auth(String username, String passwd) {
        JsonObject loginJsonObject = jsonHandler.createLoginRequest(username, passwd);
        String responseString = sendRequest(loginUrl, null, null, null, loginJsonObject);
        return jsonHandler.parseLoginResponse(responseString);
    }

    public MeResponse me(String rcUid, String rcToken) {
        String responseString = sendRequest(meUrl, rcUid, rcToken, hostUrl, null);
        return jsonHandler.parseMeResponse(responseString);
    }

    private String sendRequest(String url, String rcUid, String rcToken, String host, JsonObject requestJsonObject) {
        OkHttpClient okHttp = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url(url);

        if (rcUid != null) {
            requestBuilder.addHeader(rcUidHeader, rcUid);
        }
        if (rcToken != null) {
            requestBuilder.addHeader(rcTokenHeader, rcToken);
        }
        if (host != null) {
            requestBuilder.addHeader("Host", host);
        }

        if (requestJsonObject != null) {
            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestJsonObject.toString()
            );
            requestBuilder.post(requestBody);
        }

        try {
            Request request = requestBuilder.build();
            Response response = okHttp.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            return null;
        }
    }
}
