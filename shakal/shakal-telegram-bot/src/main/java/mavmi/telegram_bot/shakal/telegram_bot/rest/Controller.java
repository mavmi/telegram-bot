package mavmi.telegram_bot.shakal.telegram_bot.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.common.MessageJson;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.impl.shakal.telegram_bot.ShakalTelegramBotRq;
import mavmi.telegram_bot.common.dto.impl.shakal.telegram_bot.ShakalTelegramBotRs;
import mavmi.telegram_bot.common.httpFilter.UserSessionHttpFilter;
import mavmi.telegram_bot.shakal.telegram_bot.bot.Bot;
import mavmi.telegram_bot.shakal.telegram_bot.httpClient.HttpClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
public class Controller {

    private final Bot bot;
    private final HttpClient httpClient;

    public Controller(Bot bot, HttpClient httpClient) {
        this.bot = bot;
        this.httpClient = httpClient;
    }

    @PostMapping("/sendText")
    public ResponseEntity<ShakalTelegramBotRs> sendText(@RequestBody ShakalTelegramBotRq shakalTelegramBotRq) {
        log.info("Got request on /sendText");

        MessageJson messageJson = shakalTelegramBotRq.getMessageJson();
        if (messageJson == null) {
            log.error("Service message is null");
            return new ResponseEntity<ShakalTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()));
        }

        long chatId = shakalTelegramBotRq.getChatId();
        String msg = messageJson.getTextMessage();

        bot.sendMessage(chatId, msg, ParseMode.Markdown);

        return new ResponseEntity<ShakalTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendKeyboard")
    public ResponseEntity<ShakalTelegramBotRs> sendKeyboard(@RequestBody ShakalTelegramBotRq shakalTelegramBotRq) {
        log.info("Got request on /sendKeyboard");

        long chatId = shakalTelegramBotRq.getChatId();
        String msg = shakalTelegramBotRq.getMessageJson().getTextMessage();
        String[] buttons = shakalTelegramBotRq.getKeyboardJson().getKeyboardButtons();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : buttons) {
            replyKeyboardMarkup.addRow(button);
        }

        bot.sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));

        return new ResponseEntity<ShakalTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @SneakyThrows
    @PostMapping("/sendDice")
    public ResponseEntity<ShakalTelegramBotRs> sendDice(@RequestBody ShakalTelegramBotRq shakalTelegramBotRq) {
        log.info("Got request on /sendDice");

        long chatId = shakalTelegramBotRq.getChatId();
        String msg = shakalTelegramBotRq.getMessageJson().getTextMessage();
        String[] buttons = shakalTelegramBotRq.getKeyboardJson().getKeyboardButtons();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(buttons)
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "")
                .replyMarkup(replyKeyboardMarkup));

        int botDiceValue = bot.sendRequest(new SendDice(chatId)).message().dice().value();
        ObjectMapper objectMapper = new ObjectMapper();

        DiceJson diceJson = DiceJson
                .builder()
                .botDiceValue(botDiceValue)
                .build();
        ShakalServiceRq shakalServiceRq = ShakalServiceRq
                .builder()
                .chatId(chatId)
                .diceJson(diceJson)
                .build();

        String requestBody = objectMapper.writeValueAsString(shakalServiceRq);
        int statusCode = httpClient.sendRequest(
                httpClient.serviceUrl,
                httpClient.serviceProcessRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, String.valueOf(chatId)),
                requestBody
        ).code();

        return new ResponseEntity<ShakalTelegramBotRs>(HttpStatusCode.valueOf(statusCode));
    }
}
