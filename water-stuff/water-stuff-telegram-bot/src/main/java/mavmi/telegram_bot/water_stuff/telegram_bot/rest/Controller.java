package mavmi.telegram_bot.water_stuff.telegram_bot.rest;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.impl.water_stuff.service.WaterStuffServiceDtoRs;
import mavmi.telegram_bot.common.dto.impl.water_stuff.telegram_bot.WaterStuffTelegramBotRq;
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
    public ResponseEntity<WaterStuffServiceDtoRs> sendText(@RequestBody WaterStuffTelegramBotRq waterStuffTelegramBotRq) {
        log.info("Got request on /sendText");

        long chatId = waterStuffTelegramBotRq.getChatId();
        String msg = waterStuffTelegramBotRq.getMessageJson().getTextMessage();

        bot.sendMessage(new SendMessage(chatId, msg).parseMode(ParseMode.Markdown));

        return new ResponseEntity<WaterStuffServiceDtoRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendKeyboard")
    public ResponseEntity<WaterStuffServiceDtoRs> sendKeyboard(@RequestBody WaterStuffTelegramBotRq waterStuffTelegramBotRq) {
        log.info("Got request on /sendKeyboard");

        long chatId = waterStuffTelegramBotRq.getChatId();
        String msg = waterStuffTelegramBotRq.getMessageJson().getTextMessage();
        String[] buttons = waterStuffTelegramBotRq.getKeyboardJson().getKeyboardButtons();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : buttons) {
            replyKeyboardMarkup.addRow(button);
        }

        bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "")
                .replyMarkup(replyKeyboardMarkup)
                .parseMode(ParseMode.Markdown));

        return new ResponseEntity<WaterStuffServiceDtoRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
