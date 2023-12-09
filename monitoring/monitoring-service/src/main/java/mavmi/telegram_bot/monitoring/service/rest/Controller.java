package mavmi.telegram_bot.monitoring.service.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.monitoring.service.http.HttpClient;
import mavmi.telegram_bot.monitoring.service.service.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
    public ResponseEntity<String> sendText(@RequestBody String content) {
        log.info("Got request on /sendText");

        List<Long> idx = service.getAvailableIdx();
        if (idx == null) {
            log.error("Chat idx list is empty");
            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        httpClient.sendText(idx, decode(content));
        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendFile")
    public ResponseEntity<String> sendFile(@RequestBody String content) {
        log.info("Got request on /sendFile");

        List<Long> idx = service.getAvailableIdx();
        if (idx == null) {
            log.error("Chat idx list is empty");
            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        httpClient.sendFile(idx, decode(content));
        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    private String decode(String str){
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        return str.substring(0, str.length() - 1);
    }
}
