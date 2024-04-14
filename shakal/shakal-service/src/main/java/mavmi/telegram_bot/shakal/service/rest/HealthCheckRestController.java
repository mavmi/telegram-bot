package mavmi.telegram_bot.shakal.service.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthCheckRestController {

    @GetMapping(path = "/healthcheck")
    public ResponseEntity<String> healthCheck() {
        log.info("Got /healthcheck request");
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
