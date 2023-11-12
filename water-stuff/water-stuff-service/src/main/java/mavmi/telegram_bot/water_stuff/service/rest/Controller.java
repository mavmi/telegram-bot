package mavmi.telegram_bot.water_stuff.service.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.water_stuff.service.service.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

    private final Service service;
    private final ObjectMapper objectMapper;

    public Controller(Service service) {
        this.service = service;
        this.objectMapper = new ObjectMapper();
    }

    @PostMapping("/processRequest")
    public void processRequest(@RequestBody String body) {
        log.info("Got request on /processRequest");

        try {
            BotRequestJson jsonDto = objectMapper.readValue(body, BotRequestJson.class);
            service.handleRequest(jsonDto);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.out);
        }
    }
}
