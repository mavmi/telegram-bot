package mavmi.telegram_bot.monitoring.client.httpClient;

import mavmi.telegram_bot.lib.monitoring_client_module_dto.dto.MonitoringTelegramBotRq;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MonitoringTelegramBotHttpClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String notifyEndpoint;

    public MonitoringTelegramBotHttpClient(SslBundles sslBundles,
                                           RestTemplateBuilder restTemplateBuilder,
                                           @Value("${monitoring-telegram-bot.client.http-client.ssl-bundle-name}") String sslBundleName,
                                           @Value("${monitoring-telegram-bot.client.http-client.url.base}") String baseUrl,
                                           @Value("${monitoring-telegram-bot.client.http-client.endpoint.notify}") String notifyEndpoint) {
        restTemplateBuilder = restTemplateBuilder.setSslBundle(sslBundles.getBundle(sslBundleName));

        this.restTemplate = restTemplateBuilder.build();
        this.baseUrl = baseUrl;
        this.notifyEndpoint = notifyEndpoint;
    }

    public void notify(String from, String msg) {
        MonitoringTelegramBotRq rq = MonitoringTelegramBotRq.builder()
                .from(from)
                .message(msg)
                .build();

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MonitoringTelegramBotRq> httpEntity = new HttpEntity<>(rq, httpHeaders);

        restTemplate.postForLocation(baseUrl + notifyEndpoint, httpEntity);
    }
}
