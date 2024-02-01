package mavmi.telegram_bot.shakal.service.rest;

import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.httpFilter.session.UserSession;
import mavmi.telegram_bot.shakal.service.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private UserSession userSession;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping("/processRequest")
    public ResponseEntity<ShakalServiceRs> processRequest(@RequestBody ShakalServiceRq shakalServiceRq) {
        log.info("Got request on /processRequest");
        service.handleRequest(shakalServiceRq);
        return new ResponseEntity<ShakalServiceRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
