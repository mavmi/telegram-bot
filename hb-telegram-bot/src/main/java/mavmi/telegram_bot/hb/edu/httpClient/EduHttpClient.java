package mavmi.telegram_bot.hb.edu.httpClient;

import lombok.SneakyThrows;
import mavmi.telegram_bot.hb.edu.dto.EduAuthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.endpoint.jackson.EndpointObjectMapper;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class EduHttpClient {

    private final RestTemplate restTemplate = new RestTemplate();
    private final EndpointObjectMapper endpointObjectMapper;

    @Value("${http-client.username-postfix}")
    private String usernamePostfix;
    @Value("${http-client.edu.auth.url}")
    private String authUrl;
    @Value("${http-client.edu.auth.username}")
    private String authUsername;
    @Value("${http-client.edu.auth.password}")
    private String authPassword;
    @Value("${http-client.edu.api.url}")
    private String apiUrl;
    @Value("${http-client.edu.api.get-peer-path}")
    private String getPeerPath;

    public EduHttpClient(EndpointObjectMapper endpointObjectMapper) {
        this.endpointObjectMapper = endpointObjectMapper;
    }

    @Nullable
    @SneakyThrows
    synchronized public String auth() {
        MultiValueMap<String, String> bodyParams = new LinkedMultiValueMap<>();
        bodyParams.add("username", authUsername);
        bodyParams.add("password", authPassword);
        bodyParams.add("grant_type", "password");
        bodyParams.add("client_id", "s21-open-api");

        ResponseEntity<EduAuthResponse> responseEntity = restTemplate.postForEntity(authUrl, bodyParams, EduAuthResponse.class);
        if (responseEntity.getStatusCode().equals(HttpStatusCode.valueOf(200))) {
            return responseEntity.getBody().getAccessToken();
        } else {
            return null;
        }
    }

    synchronized public int verifyPeerName(String name, String token) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + token);

        HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    apiUrl + getPeerPath + "/" + name + usernamePostfix,
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            return responseEntity.getStatusCode().value();
        } catch (Exception e) {
            return 404;
        }
    }
}
