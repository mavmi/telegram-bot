package mavmi.telegram_bot.hb.edu.httpClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope("thread")
public class EduHttpClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("http-client.username-postfix")
    private String usernamePostfix;
    @Value("http-client.edu.auth.url")
    private String authUrl;
    @Value("http-client.edu.auth.username")
    private String authUsername;
    @Value("http-client.edu.auth.password")
    private String authPassword;
    @Value("http-client.edu.api.url")
    private String apiUrl;
    @Value("http-client.edu.api.get-peer-path")
    private String getPeerPath;


}
