package mavmi.telegram_bot.monitoring.telegram_bot.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRq;
import mavmi.telegram_bot.common.dto.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRs;
import mavmi.telegram_bot.monitoring.telegram_bot.telegramBot.MonitoringTelegramBot;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller {

    private final MonitoringTelegramBot monitoringTelegramBot;

    @PostMapping("/sendText")
    public ResponseEntity<MonitoringTelegramBotRs> line(@RequestBody MonitoringTelegramBotRq monitoringTelegramBotRq){
        log.info("Got request on /sendText");

        monitoringTelegramBot.sendText(
                monitoringTelegramBotRq.getChatIdx(),
                monitoringTelegramBotRq.getMessageJson().getTextMessage()
        );

        return new ResponseEntity<MonitoringTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendFile")
    public ResponseEntity<MonitoringTelegramBotRs> file(@RequestBody MonitoringTelegramBotRq monitoringTelegramBotRq){
        log.info("Got request on /sendFile");

        File fileToSend = new File(monitoringTelegramBotRq.getFileJson().getFilePath());
        monitoringTelegramBot.sendFile(
                monitoringTelegramBotRq.getChatIdx(),
                fileToSend
        );

        if (!fileToSend.delete()) {
            log.error("Cannot delete backup archive file");
        }

        return new ResponseEntity<MonitoringTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
