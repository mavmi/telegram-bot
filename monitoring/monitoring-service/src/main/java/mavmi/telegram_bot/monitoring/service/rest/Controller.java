package mavmi.telegram_bot.monitoring.service.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.secured.annotation.Secured;
import mavmi.telegram_bot.monitoring.service.http.HttpClient;
import mavmi.telegram_bot.monitoring.service.service.Service;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class Controller {

    private final Service service;
    private final HttpClient httpClient;

    public Controller(
            Service service,
            HttpClient httpClient
    ) {
        this.service = service;
        this.httpClient = httpClient;
    }

    @PostMapping("/sendText")
    public ResponseEntity<String> sendText(@RequestBody ServiceRequestJson serviceRequestJson) {
        log.info("Got request on /sendText");

        Long chatId = serviceRequestJson.getChatId();
        List<Long> chatIdx = (chatId == null) ? service.getAvailableIdx() : List.of(chatId);
        String content = serviceRequestJson.getServiceMessageJson().getTextMessage();
        int code = httpClient.sendText(chatIdx, content);

        return new ResponseEntity<String>(HttpStatusCode.valueOf(code));
    }

    @PostMapping("/sendFile")
    public ResponseEntity<String> sendFile(@RequestBody ServiceRequestJson serviceRequestJson) {
        log.info("Got request on /sendFile");

        Long chatId = serviceRequestJson.getChatId();
        List<Long> chatIdx = (chatId == null) ? service.getAvailableIdx() : List.of(chatId);
        String content = serviceRequestJson.getServiceFileJson().getFilePath();
        int code = httpClient.sendFile(chatIdx, content);

        return new ResponseEntity<String>(HttpStatusCode.valueOf(code));
    }

    @Secured(BOT_NAME.MONITORING_BOT)
    @PostMapping("/putTask")
    public ResponseEntity<String> putTask(@RequestBody BotRequestJson botRequestJson) {
        log.info("Got request on /putTask");
        int statusCode = service.putTask(botRequestJson);
        return new ResponseEntity<String>(HttpStatusCode.valueOf(statusCode));
    }
}
