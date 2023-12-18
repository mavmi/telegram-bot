package mavmi.telegram_bot.water_stuff.service.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.secured.annotation.Secured;
import mavmi.telegram_bot.water_stuff.service.service.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @Secured(BOT_NAME.WATER_STUFF_BOT)
    @PostMapping("/processRequest")
    public ResponseEntity<String> processRequest(@RequestBody BotRequestJson jsonDto) {
        log.info("Got request on /processRequest");
        service.handleRequest(jsonDto);
        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
