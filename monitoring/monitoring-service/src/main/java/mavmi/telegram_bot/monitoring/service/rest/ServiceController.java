package mavmi.telegram_bot.monitoring.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRq;
import mavmi.telegram_bot.common.dto.impl.monitoring.service.MonitoringServiceRs;
import mavmi.telegram_bot.common.secured.annotation.Secured;
import mavmi.telegram_bot.monitoring.service.httpClient.HttpClient;
import mavmi.telegram_bot.monitoring.service.service.Service;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final Service service;
    private final HttpClient httpClient;

    @PostMapping("/sendText")
    public ResponseEntity<MonitoringServiceRs> sendText(@RequestBody MonitoringServiceRq monitoringServiceRq) {
        log.info("Got request on /sendText");

        Long chatId = monitoringServiceRq.getChatId();
        List<Long> chatIdx = (chatId == null) ? service.getAvailableIdx() : List.of(chatId);
        String content = monitoringServiceRq.getMessageJson().getTextMessage();
        int code = httpClient.sendText(chatIdx, content).code();

        return new ResponseEntity<MonitoringServiceRs>(HttpStatusCode.valueOf(code));
    }

    @PostMapping("/sendFile")
    public ResponseEntity<MonitoringServiceRs> sendFile(@RequestBody MonitoringServiceRq monitoringServiceRq) {
        log.info("Got request on /sendFile");

        Long chatId = monitoringServiceRq.getChatId();
        List<Long> chatIdx = (chatId == null) ? service.getAvailableIdx() : List.of(chatId);
        String content = monitoringServiceRq.getFileJson().getFilePath();
        int code = httpClient.sendFile(chatIdx, content).code();

        return new ResponseEntity<MonitoringServiceRs>(HttpStatusCode.valueOf(code));
    }

    @Secured(BOT_NAME.MONITORING_BOT)
    @PostMapping("/processRequest")
    public ResponseEntity<MonitoringServiceRs> processRequest(@RequestBody MonitoringServiceRq monitoringServiceRq) {
        log.info("Got request on /processRequest");

        int statusCode = service.handleRequest(monitoringServiceRq);
        return new ResponseEntity<MonitoringServiceRs>(HttpStatusCode.valueOf(statusCode));
    }
}