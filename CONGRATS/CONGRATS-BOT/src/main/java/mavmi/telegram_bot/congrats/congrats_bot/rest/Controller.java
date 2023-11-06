package mavmi.telegram_bot.congrats.congrats_bot.rest;

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.congrats.congrats_bot.telegram_bot.Bot;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@Slf4j
@RestController
public class Controller {
    private final Bot bot;

    public Controller(Bot bot) {
        this.bot = bot;
    }

    @PostMapping("/sendVoice")
    public void sendVoice(@RequestBody String input) {
        log.info("Got POST request on /sendVoice: {}", input);
        if (input == null) {
            log.error("Body is null");
            return;
        }

        String filePath = getFileName(input);
        log.info("File path: {}", filePath);

        if (filePath != null) {
            bot.sendVoice(filePath);
            new File(filePath).delete();
        }
    }

    @PostMapping("/sendVideoNote")
    public void sendVideoNote(@RequestBody String input) {
        log.info("Got POST request on /sendVideoNote: {}", input);
        if (input == null) {
            log.error("Body is null");
            return;
        }

        String filePath = getFileName(input);
        log.info("File path: {}", filePath);

        if (filePath != null) {
            bot.sendVideoNote(filePath);
            new File(filePath).delete();
        }
    }

    @Nullable
    private String getFileName(@RequestBody String line) {
        try {
            return JsonParser.parseString(line)
                    .getAsJsonObject()
                    .get("name")
                    .getAsString();
        } catch (JsonSyntaxException e) {
            e.printStackTrace(System.err);
            return null;
        }
    }
}
