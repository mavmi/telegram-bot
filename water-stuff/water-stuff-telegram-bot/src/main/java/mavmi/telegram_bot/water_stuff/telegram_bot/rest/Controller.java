package mavmi.telegram_bot.water_stuff.telegram_bot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceKeyboardJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.water_stuff.telegram_bot.bot.Bot;
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
    public void sendText(@RequestBody String body) {
        log.info("Got request on /sendText");

        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();

            if (serviceMessageJson == null) {
                log.error("Service message is null");
                return;
            }

            long chatId = serviceRequestJson.getChatId();
            String msg = serviceMessageJson.getTextMessage();

            bot.sendMessage(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));
        } catch (JsonProcessingException | NullPointerException e) {
            log.error("Error while parsing json string");
            e.printStackTrace(System.out);
        }
    }

    @PostMapping("/sendKeyboard")
    public void sendKeyboard(@RequestBody String body) {
        log.info("Got request on /sendKeyboard");

        try {
            ServiceRequestJson serviceRequestJson = objectMapper.readValue(body, ServiceRequestJson.class);
            ServiceMessageJson serviceMessageJson = serviceRequestJson.getServiceMessageJson();
            ServiceKeyboardJson serviceKeyboardJson = serviceRequestJson.getServiceKeyboardJson();

            if (serviceMessageJson == null) {
                log.error("Service message is null");
                return;
            }
            if (serviceKeyboardJson == null) {
                log.error("Keyboard is null");
                return;
            }

            long chatId = serviceRequestJson.getChatId();
            String msg = serviceMessageJson.getTextMessage();
            String[] buttons = serviceKeyboardJson.getKeyboardButtons();

            ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(buttons)
                    .oneTimeKeyboard(true)
                    .resizeKeyboard(true);

            bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "").replyMarkup(replyKeyboardMarkup));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json string");
            e.printStackTrace(System.out);
        }
    }
}
