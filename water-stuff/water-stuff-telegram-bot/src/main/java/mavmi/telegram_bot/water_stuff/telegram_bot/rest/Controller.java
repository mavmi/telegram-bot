package mavmi.telegram_bot.water_stuff.telegram_bot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceKeyboardJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
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

    private final ObjectMapper objectMapper;
    private final Bot bot;

    public Controller(Bot bot) {
        this.objectMapper = new ObjectMapper();
        this.bot = bot;
    }

    @PostMapping("/sendText")
    public ResponseEntity<String> sendText(@RequestBody String body) {
        log.info("Got request on /sendText");

        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();

            if (serviceMessageJson == null) {
                log.error("Service message is null");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }

            long chatId = serviceRequestJson.getChatId();
            String msg = serviceMessageJson.getTextMessage();

            bot.sendMessage(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
        } catch (JsonProcessingException | NullPointerException e) {
            log.error("Error while parsing json string");
            e.printStackTrace(System.out);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/sendKeyboard")
    public ResponseEntity<String> sendKeyboard(@RequestBody String body) {
        log.info("Got request on /sendKeyboard");

        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
            ServiceKeyboardJson serviceKeyboardJson = serviceRequestJson.getServiceKeyboardJson();

            if (serviceMessageJson == null) {
                log.error("Service message is null");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }
            if (serviceKeyboardJson == null) {
                log.error("Keyboard is null");
                return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
            }

            long chatId = serviceRequestJson.getChatId();
            String msg = serviceMessageJson.getTextMessage();
            String[] buttons = serviceKeyboardJson.getKeyboardButtons();

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                    .oneTimeKeyboard(true)
                    .resizeKeyboard(true);

            for (String button : buttons) {
                replyKeyboardMarkup.addRow(button);
            }

            bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "").replyMarkup(replyKeyboardMarkup));

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json string");
            e.printStackTrace(System.out);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}