package mavmi.telegram_bot.monitoring.telegram_bot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceFileJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.monitoring.telegram_bot.bot.Bot;
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
    public void line(@RequestBody String body){
        log.info("Got request on /sendText");

        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
            List<Long> chatIdx = serviceRequestJson.getChatIdx();

            if (chatIdx == null) {
                log.error("Chat idx list is empty");
                return;
            }
            if (serviceMessageJson == null) {
                log.error("Service message is null");
                return;
            }

            String msg = serviceMessageJson.getTextMessage();
            bot.sendMessage(chatIdx, msg);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.err);
        }
    }

    @PostMapping("/sendFile")
    public void file(@RequestBody String body){
        log.info("Got request on /sendText");

        File file = null;
        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceFileJson serviceFileJson = serviceRequestJson.getServiceFileJson();
            List<Long> chatIdx = serviceRequestJson.getChatIdx();

            if (chatIdx == null) {
                log.error("Chat idx list is empty");
                return;
            }
            if (serviceFileJson == null) {
                log.error("Service message is null");
                return;
            }

            String filePath = serviceFileJson.getFilePath();
            file = new File(filePath);
            bot.sendFile(chatIdx, file);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.err);
        } finally {
            if (file != null && !file.delete()) {
                log.error("Cannot delete backup archive file");
            }
        }
    }

    private String decode(String str){
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        return str.substring(0, str.length() - 1);
    }
}
