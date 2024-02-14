package mavmi.telegram_bot.monitoring.telegram_bot.rest;

import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.common.FileJson;
import mavmi.telegram_bot.common.dto.common.KeyboardJson;
import mavmi.telegram_bot.common.dto.common.UserMessageJson;
import mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRq;
import mavmi.telegram_bot.common.dto.impl.monitoring.telegram_bot.MonitoringTelegramBotRs;
import mavmi.telegram_bot.monitoring.telegram_bot.bot.Bot;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

@Slf4j
@RestController
public class Controller {
    private final Bot bot;

    public Controller(Bot bot){
        this.bot = bot;
    }

    @PostMapping("/sendText")
    public ResponseEntity<MonitoringTelegramBotRs> line(@RequestBody MonitoringTelegramBotRq monitoringTelegramBotRq){
        log.info("Got request on /sendText");

        UserMessageJson userMessageJson = monitoringTelegramBotRq.getUserMessageJson();
        List<Long> chatIdx = monitoringTelegramBotRq.getChatIdx();
        String msg = userMessageJson.getTextMessage();
        bot.sendMessage(chatIdx, msg);

        return new ResponseEntity<MonitoringTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendFile")
    public ResponseEntity<MonitoringTelegramBotRs> file(@RequestBody MonitoringTelegramBotRq monitoringTelegramBotRq){
        log.info("Got request on /sendText");

        FileJson fileJson = monitoringTelegramBotRq.getFileJson();
        List<Long> chatIdx = monitoringTelegramBotRq.getChatIdx();
        String filePath = fileJson.getFilePath();
        File file = new File(filePath);
        bot.sendFile(chatIdx, file);

        if (!file.delete()) {
            log.error("Cannot delete backup archive file");
        }

        return new ResponseEntity<MonitoringTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }

    @PostMapping("/sendKeyboard")
    public ResponseEntity<MonitoringTelegramBotRs> sendKeyboard(@RequestBody MonitoringTelegramBotRq monitoringTelegramBotRq) {
        log.info("Got request on /sendKeyboard");

        UserMessageJson userMessageJson = monitoringTelegramBotRq.getUserMessageJson();
        KeyboardJson keyboardJson = monitoringTelegramBotRq.getKeyboardJson();
        long chatId = monitoringTelegramBotRq.getChatId();
        String msg = userMessageJson.getTextMessage();
        String[] buttons = keyboardJson.getKeyboardButtons();

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : buttons) {
            replyKeyboardMarkup.addRow(button);
        }

        bot.sendMessage(new SendMessage(chatId, (msg != null) ? msg : "")
                .replyMarkup(replyKeyboardMarkup)
                .parseMode(ParseMode.Markdown));

        return new ResponseEntity<MonitoringTelegramBotRs>(HttpStatusCode.valueOf(HttpStatus.OK.value()));
    }
}
