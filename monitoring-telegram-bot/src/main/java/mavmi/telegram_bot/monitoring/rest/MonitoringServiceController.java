package mavmi.telegram_bot.monitoring.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.monitoring.service.MonitoringService;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRq;
import mavmi.telegram_bot.monitoring.service.dto.monitoringService.MonitoringServiceRs;
import mavmi.telegram_bot.monitoring.telegramBot.client.MonitoringTelegramBotSender;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/monitoring-service")
@RequiredArgsConstructor
public class MonitoringServiceController {

    private final MonitoringService monitoringService;
    private final MonitoringTelegramBotSender sender;

    @PostMapping("/sendText")
    public ResponseEntity<MonitoringServiceRs> sendText(@RequestBody MonitoringServiceRq monitoringServiceRq) {
        log.info("Got request on /sendText");

        Long chatId = monitoringServiceRq.getChatId();
        List<Long> chatIdx = (chatId == null) ? monitoringService.getAvailableIdx() : List.of(chatId);
        String content = monitoringServiceRq.getMessageJson().getTextMessage();
        sender.sendText(chatIdx, content);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/sendFile")
    public ResponseEntity<MonitoringServiceRs> sendFile(@RequestBody MonitoringServiceRq monitoringServiceRq) {
        log.info("Got request on /sendFile");

        Long chatId = monitoringServiceRq.getChatId();
        List<Long> chatIdx = (chatId == null) ? monitoringService.getAvailableIdx() : List.of(chatId);
        String content = monitoringServiceRq.getFileJson().getFilePath();
        sender.sendFile(chatIdx, new File(content));

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
