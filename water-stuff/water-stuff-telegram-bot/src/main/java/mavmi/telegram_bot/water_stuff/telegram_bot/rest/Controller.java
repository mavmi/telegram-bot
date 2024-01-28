package mavmi.telegram_bot.water_stuff.telegram_bot.rest;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceKeyboardJson;
import mavmi.telegram_bot.common.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.water_stuff.telegram_bot.bot.Bot;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class Controller {

    private final Bot bot;

    public Controller(Bot bot) {
        this.bot = bot;
    }

    @PostMapping("/sendText")
    public ResponseEntity<String> sendText(@RequestBody ServiceRequestJson serviceRequestJson) {
        log.info("Got request on /sendText");

        ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
        long chatId = serviceRequestJson.getChatId();
        String msg = serviceMessageJson.getTextMessage();

        bot.sendMessage(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));

        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendKeyboard")
    public ResponseEntity<String> sendKeyboard(@RequestBody ServiceRequestJson serviceRequestJson) {
        log.info("Got request on /sendKeyboard");

        ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
        ServiceKeyboardJson serviceKeyboardJson = serviceRequestJson.getServiceKeyboardJson();
        long chatId = serviceRequestJson.getChatId();
        String msg = serviceMessageJson.getTextMessage();
        String[] buttons = serviceKeyboardJson.getKeyboardButtons();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : buttons) {
            replyKeyboardMarkup.addRow(button);
        }

        bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "").replyMarkup(replyKeyboardMarkup).parseMode(ParseMode.Markdown));

        return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
