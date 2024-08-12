package mavmi.telegram_bot.rocketchat.httpClient;

import mavmi.telegram_bot.rocketchat.mapper.RocketchatMapper;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.SendCommandRq;
import mavmi.telegram_bot.rocketchat.service.dto.websocketClient.SendCommandRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope("thread")
public class RocketchatHttpClient {

    private final RocketchatMapper rocketchatMapper;
    private final RestTemplate restTemplate;
    private final String url;
    private final String sendCommandEndpoint;
    private final String qrCommand;

    public RocketchatHttpClient(
            RocketchatMapper rocketchatMapper,
            @Value("${http-client.url}") String url,
            @Value("${http-client.send-command-endpoint}") String sendCommandEndpoint,
            @Value("${http-client.commands.qr}") String qrCommand
    ) {
        this.rocketchatMapper = rocketchatMapper;
        this.restTemplate = new RestTemplate();
        this.url = url;
        this.sendCommandEndpoint = sendCommandEndpoint;
        this.qrCommand = qrCommand;
    }

    @Nullable
    public SendCommandRs sendQrCommand(String rocketchatUserId, String rocketchatUserToken, String roomId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("X-Auth-Token", rocketchatUserToken);
        headers.add("X-User-Id", rocketchatUserId);

        SendCommandRq sendCommandRequest = rocketchatMapper.createSendCommandRequest(roomId, qrCommand);

        HttpEntity<SendCommandRq> request = new HttpEntity<>(sendCommandRequest, headers);

        return restTemplate.postForEntity(url + sendCommandEndpoint, request, SendCommandRs.class).getBody();
    }
}
