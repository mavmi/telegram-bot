package mavmi.telegram_bot.water_stuff.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.reminder_service.ReminderServiceRs;
import mavmi.telegram_bot.water_stuff.service.service.reminder.ReminderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/reminder-service")
public class ReminderServiceController {

    private final ReminderService reminderService;

    @GetMapping("/processRequest")
    public ResponseEntity<ReminderServiceRs> processRequest() {
        log.info("Got request on /reminder-service/processRequest");
        return new ResponseEntity<ReminderServiceRs>(
                reminderService.handleRequest(),
                HttpStatusCode.valueOf(HttpStatus.OK.value())
        );
    }

}
