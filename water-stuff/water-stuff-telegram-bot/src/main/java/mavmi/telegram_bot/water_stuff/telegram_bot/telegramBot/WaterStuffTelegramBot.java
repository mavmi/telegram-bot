package mavmi.telegram_bot.water_stuff.telegram_bot.telegramBot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.dto.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.water_stuff.telegram_bot.httpClient.WaterStuffTelegramBotHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class WaterStuffTelegramBot extends TelegramBot {

    private final WaterStuffTelegramBotHttpClient waterStuffTelegramBotHttpClient;

    public WaterStuffTelegramBot(
            WaterStuffTelegramBotHttpClient waterStuffTelegramBotHttpClient,
            @Value("${telegram-bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.waterStuffTelegramBotHttpClient = waterStuffTelegramBotHttpClient;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            @SneakyThrows
            public int process(List<Update> updates) {
                for (Update update : updates) {
                    log.info("Got request from id {}", update.message().from().id());

                    long chatId = update.message().from().id();
                    ResponseEntity<WaterStuffServiceRs> response = waterStuffTelegramBotHttpClient.waterStuffServiceRequest(update.message());

                    if (response.getStatusCode().equals(HttpStatusCode.valueOf(HttpStatus.OK.value()))) {
                        WaterStuffServiceRs waterStuffServiceRs = response.getBody();

                        switch (waterStuffServiceRs.getWaterStuffServiceTask()) {
                            case SEND_TEXT -> sendText(chatId, waterStuffServiceRs.getMessageJson().getTextMessage());
                            case SEND_KEYBOARD -> sendKeyboard(
                                    chatId,
                                    waterStuffServiceRs.getMessageJson().getTextMessage(),
                                    waterStuffServiceRs.getKeyboardJson().getKeyboardButtons()
                            );
                        }
                    } else {
                        WaterStuffTelegramBot.this.sendMessage(chatId, "Service unavailable");
                    }
                }

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
            e.printStackTrace(System.out);
        });
    }

    public void sendText(long chatId, String msg) {
        sendMessage(chatId, msg, ParseMode.Markdown);
    }

    public void sendKeyboard(long chatId, String msg, String[] keyboardButtons) {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .oneTimeKeyboard(true)
                .resizeKeyboard(true);

        for (String button : keyboardButtons) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg)
                .replyMarkup(replyKeyboardMarkup)
                .parseMode(ParseMode.Markdown));
    }
}
