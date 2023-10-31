package mavmi.telegram_bot.monitoring_bot.controller;

import mavmi.telegram_bot.monitoring_bot.telegram_bot.Bot;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping(path = "/monitoring", method = RequestMethod.POST)
public class Controller {
    private final Bot bot;

    public Controller(Bot bot){
        this.bot = bot;
    }

    @RequestMapping(path = "/line")
    public void line(
            @RequestBody String msg
    ){
        try {
            bot.sendMessage(decode(msg));
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
    }

    @RequestMapping(path = "/file")
    public void file(
            @RequestBody String filepath
    ){
        File file = null;

        try {
            file = new File(decode(filepath));
            bot.sendFile(file);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        } finally {
            if (file != null) {
                file.delete();
            }
        }
    }

    private String decode(String str){
        str = URLDecoder.decode(str, StandardCharsets.UTF_8);
        return str.substring(0, str.length() - 1);
    }
}
