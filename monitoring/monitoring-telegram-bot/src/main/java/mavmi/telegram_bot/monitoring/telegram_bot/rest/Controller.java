package mavmi.telegram_bot.monitoring.telegram_bot.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceFileJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.monitoring.telegram_bot.bot.Bot;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
public class Controller {
    private final Bot bot;

    public Controller(Bot bot){
        this.bot = bot;
    }

    @PostMapping("/sendText")
    public ResponseEntity<String> line(@RequestBody ServiceRequestJson serviceRequestJson){
        log.info("Got request on /sendText");

        ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
        List<Long> chatIdx = serviceRequestJson.getChatIdx();
        String msg = serviceMessageJson.getTextMessage();
        bot.sendMessage(chatIdx, msg);

        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendFile")
    public ResponseEntity<String> file(@RequestBody ServiceRequestJson serviceRequestJson){
        log.info("Got request on /sendText");

        ServiceFileJson serviceFileJson = serviceRequestJson.getServiceFileJson();
        List<Long> chatIdx = serviceRequestJson.getChatIdx();
        String filePath = serviceFileJson.getFilePath();
        File file = new File(filePath);
        bot.sendFile(chatIdx, file);

        if (!file.delete()) {
            log.error("Cannot delete backup archive file");
        }

        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    private String decode(String str){
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        return str.substring(0, str.length() - 1);
    }
}
