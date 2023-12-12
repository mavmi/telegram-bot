package mavmi.telegram_bot.shakal.telegram_bot.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.utils.dto.json.bot.BotRequestJson;
import mavmi.telegram_bot.common.utils.dto.json.bot.inner.DiceJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceKeyboardJson;
import mavmi.telegram_bot.common.utils.dto.json.service.inner.ServiceMessageJson;
import mavmi.telegram_bot.common.utils.dto.json.service.ServiceRequestJson;
import mavmi.telegram_bot.shakal.telegram_bot.bot.Bot;
import mavmi.telegram_bot.shakal.telegram_bot.http.HttpClient;
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
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public Controller(Bot bot, HttpClient httpClient) {
        this.bot = bot;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
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

            bot.sendMessage(chatId, msg, ParseMode.Markdown);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
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
                    .resizeKeyboard(true)
                    .oneTimeKeyboard(true);
            for (String button : buttons) {
                replyKeyboardMarkup.addRow(button);
            }

            bot.sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.out);

            return new ResponseEntity<String>(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PostMapping("/sendDice")
    public void sendDice(@RequestBody String body) {
        log.info("Got request on /sendDice");

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

            bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "")
                    .replyMarkup(replyKeyboardMarkup));

            int botDiceValue = bot.sendRequest(new SendDice(chatId)).message().dice().value();

            httpClient.sendRequest(
                    httpClient.serviceUrl,
                    httpClient.serviceProcessRequestEndpoint,
                    BotRequestJson
                            .builder()
                            .chatId(chatId)
                            .diceJson(
                                    DiceJson
                                            .builder()
                                            .botDiceValue(botDiceValue)
                                            .build()
                            )
                            .build()
            );
        } catch (JsonProcessingException e) {
            log.error("Error while parsing json body: {}", body);
            e.printStackTrace(System.out);
        }
    }
}
