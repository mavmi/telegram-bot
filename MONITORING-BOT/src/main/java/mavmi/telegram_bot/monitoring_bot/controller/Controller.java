package mavmi.telegram_bot.monitoring_bot.controller;

import mavmi.telegram_bot.common.logger.Logger;
import mavmi.telegram_bot.monitoring_bot.telegram_bot.Bot;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(path = "/monitoring", method = RequestMethod.POST)
public class Controller {
    private final Bot bot;
    private final Logger logger;

    public Controller(Bot bot, Logger logger){
        this.bot = bot;
        this.logger = logger;
    }

    @RequestMapping(path = "/info")
    public void login(
            @RequestBody String body
    ){
        try {
            body = URLDecoder.decode(body, StandardCharsets.UTF_8);
            body = body.substring(0, body.length() - 1);
            bot.sendMsg(body);
        } catch (RuntimeException e) {
            logger.err(e.getMessage());
        }
    }
}
