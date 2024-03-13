package mavmi.telegram_bot.shakal.service.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.shakal.service.service.shakal.ShakalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/shakal-service")
public class ShakalServiceController {

    private final ShakalService shakalService;

    public ShakalServiceController(ShakalService shakalService) {
        this.shakalService = shakalService;
    }

    @PostMapping("/processRequest")
    public ResponseEntity<ShakalServiceRs> processRequest(@RequestBody ShakalServiceRq shakalServiceRq) {
        log.info("Got request on /shakal-service/processRequest");
        return new ResponseEntity<ShakalServiceRs>(
                shakalService.handleRequest(shakalServiceRq),
                HttpStatusCode.valueOf(HttpStatus.OK.value())
        );
    }
}
