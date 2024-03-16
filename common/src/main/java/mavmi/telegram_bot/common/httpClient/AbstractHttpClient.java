package mavmi.telegram_bot.common.httpClient;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

@Slf4j
public abstract class AbstractHttpClient {

    private static final int CONNECTION_TIMEOUT = 30000;

    private final RestTemplate restTemplate;

    public AbstractHttpClient(
            SslBundle sslBundle,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.restTemplate = buildRestTemplate(restTemplateBuilder, sslBundle);
    }

    public AbstractHttpClient(
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.restTemplate = buildRestTemplate(restTemplateBuilder);
    }

    public <T> ResponseEntity<T> sendPostRequest(String url, String endpoint, String requestBody, Class<T> responseType) {
        return sendPostRequest(url, endpoint, Collections.emptyMap(), requestBody, responseType);
    }

    public <T> ResponseEntity<T> sendPostRequest(String url, String endpoint, Map<String, String> headers, String requestBodyStr, Class<T> responseType) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, "application/json");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            httpHeaders.add(entry.getKey(), entry.getValue());
        }

        HttpEntity<String> entity = new HttpEntity<>(requestBodyStr, httpHeaders);

        return restTemplate.postForEntity(
                url + endpoint,
                entity,
                responseType
        );
    }

    public <T> ResponseEntity<T> sendGetRequest(String url, String endpoint, Class<T> responseType) {
        return restTemplate.getForEntity(
                url + endpoint,
                responseType
        );
    }

    protected OkHttpClient getClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.setWriteTimeout$okhttp(CONNECTION_TIMEOUT);
        builder.setReadTimeout$okhttp(CONNECTION_TIMEOUT);
        builder.setConnectTimeout$okhttp(CONNECTION_TIMEOUT);
        builder.setCallTimeout$okhttp(CONNECTION_TIMEOUT);

        return builder.build();
    }

    protected RestTemplate buildRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        Duration connectDuration = Duration.ofMillis(CONNECTION_TIMEOUT);

        return restTemplateBuilder
                .setConnectTimeout(connectDuration)
                .setReadTimeout(connectDuration)
                .build();
    }

    protected RestTemplate buildRestTemplate(RestTemplateBuilder restTemplateBuilder, SslBundle sslBundle) {
        Duration connectDuration = Duration.ofMillis(CONNECTION_TIMEOUT);

        return restTemplateBuilder
                .setConnectTimeout(connectDuration)
                .setReadTimeout(connectDuration)
                .setSslBundle(sslBundle)
                .build();
    }
}
