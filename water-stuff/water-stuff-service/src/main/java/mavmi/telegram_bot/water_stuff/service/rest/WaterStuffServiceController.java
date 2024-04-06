package mavmi.telegram_bot.water_stuff.service.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.database.auth.BOT_NAME;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRq;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.secured.annotation.Secured;
import mavmi.telegram_bot.water_stuff.service.service.water_stuff.WaterStuffService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/water-stuff-service")
@RequiredArgsConstructor
public class WaterStuffServiceController {

    private final WaterStuffService waterStuffService;

    @Secured(BOT_NAME.WATER_STUFF_BOT)
    @PostMapping("/processRequest")
    public ResponseEntity<WaterStuffServiceRs> processRequest(@RequestBody WaterStuffServiceRq waterStuffServiceRq) {
        log.info("Got request on /water-stuff-service/processRequest");
        return new ResponseEntity<WaterStuffServiceRs>(
                waterStuffService.handleRequest(waterStuffServiceRq),
                HttpStatusCode.valueOf(HttpStatus.OK.value())
        );
    }
}
