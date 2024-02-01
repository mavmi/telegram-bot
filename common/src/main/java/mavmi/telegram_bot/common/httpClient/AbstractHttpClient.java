package mavmi.telegram_bot.common.httpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;

@Slf4j
public abstract class AbstractHttpClient {

    private static final int CONNECTION_TIMEOUT = 30000;

    public Response sendRequest(String url, String endpoint, String requestBody) {
        return sendRequest(url, endpoint, Collections.emptyMap(), requestBody);
    }

    public Response sendRequest(String url, String endpoint, Map<String, String> headers, String requestBodyStr) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.setWriteTimeout$okhttp(CONNECTION_TIMEOUT);
        builder.setReadTimeout$okhttp(CONNECTION_TIMEOUT);
        builder.setConnectTimeout$okhttp(CONNECTION_TIMEOUT);
        builder.setCallTimeout$okhttp(CONNECTION_TIMEOUT);
        OkHttpClient httpClient = builder.build();

        try {
            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url + endpoint)
                    .post(requestBody);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = requestBuilder.build();

            return httpClient.newCall(request).execute();
        } catch (JsonProcessingException e) {
            log.error("Error while converting to json");
            e.printStackTrace(System.out);
            return new Response.Builder().code(HttpURLConnection.HTTP_UNAVAILABLE).build();
        } catch (IOException e) {
            log.error("Error while sending HTTP request");
            e.printStackTrace(System.out);
            return new Response.Builder().code(HttpURLConnection.HTTP_UNAVAILABLE).build();
        }
    }
}
