package mavmi.telegram_bot.monitoring.telegram_bot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceFileJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.monitoring.telegram_bot.bot.Bot;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
public class Controller {
    private final Bot bot;
    private final ObjectMapper objectMapper;

    public Controller(Bot bot){
        this.bot = bot;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/sendText")
    public ResponseEntity<String> line(@RequestBody String body){
        log.info("Got request on /sendText");

        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
            List<Long> chatIdx = serviceRequestJson.getChatIdx();

            if (chatIdx == null) {
                log.error("Chat idx list is empty");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }
            if (serviceMessageJson == null) {
                log.error("Service message is null");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }

            String msg = serviceMessageJson.getTextMessage();
            bot.sendMessage(chatIdx, msg);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.out);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/sendFile")
    public ResponseEntity<String> file(@RequestBody String body){
        log.info("Got request on /sendText");

        File file = null;
        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceFileJson serviceFileJson = serviceRequestJson.getServiceFileJson();
            List<Long> chatIdx = serviceRequestJson.getChatIdx();

            if (chatIdx == null) {
                log.error("Chat idx list is empty");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }
            if (serviceFileJson == null) {
                log.error("Service message is null");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }

            String filePath = serviceFileJson.getFilePath();
            file = new File(filePath);
            bot.sendFile(chatIdx, file);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.out);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        } finally {
            if (file != null && !file.delete()) {
                log.error("Cannot delete backup archive file");
            }
        }

        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    private String decode(String str){
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        return str.substring(0, str.length() - 1);
    }
}
