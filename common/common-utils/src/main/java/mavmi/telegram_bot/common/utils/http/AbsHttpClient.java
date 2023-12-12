package mavmi.telegram_bot.common.utils.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.IRequestJson;
import okhttp3.*;

import java.io.IOException;
import java.net.HttpURLConnection;

@Slf4j
public abstract class AbsHttpClient<R extends IRequestJson> {

    public int sendRequest(String url, String endpoint, R serviceRequestJson) {
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient httpClient = new OkHttpClient();

        try {
            String requestBodyStr = objectMapper.writeValueAsString(serviceRequestJson);

            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            RequestBody requestBody = RequestBody.create(jsonMediaType, requestBodyStr);

            Request request = new Request.Builder()
                    .url(url + endpoint)
                    .post(requestBody)
                    .build();

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
