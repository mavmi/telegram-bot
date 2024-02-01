package mavmi.telegram_bot.common.httpClient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.IRequestJson;
import okhttp3.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Collections;
import java.util.Map;

@Slf4j
public abstract class AbstractHttpClient<R extends IRequestJson> {

    public int sendRequest(String url, String endpoint, R serviceRequestJson) {
        return sendRequest(url, endpoint, Collections.emptyMap(), serviceRequestJson);
    }

    public int sendRequest(String url, String endpoint, Map<String, String> headers, R serviceRequestJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient httpClient = new OkHttpClient();

        try {
            String requestBodyStr = objectMapper.writeValueAsString(serviceRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(url + endpoint)
                    .post(requestBody);
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                requestBuilder.addHeader(entry.getKey(), entry.getValue());
            }
            Request request = requestBuilder.build();

            Response response = httpClient.newCall(request).execute();
            return response.code();
        } catch (JsonProcessingException e) {
            log.error("Error while converting to json");
            e.printStackTrace(System.out);
            return HttpURLConnection.HTTP_UNAVAILABLE;
        } catch (IOException e) {
            log.error("Error while sending HTTP request");
            e.printStackTrace(System.out);
            return HttpURLConnection.HTTP_UNAVAILABLE;
        }
    }
}
