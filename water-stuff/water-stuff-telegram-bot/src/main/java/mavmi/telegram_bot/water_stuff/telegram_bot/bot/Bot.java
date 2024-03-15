package mavmi.telegram_bot.water_stuff.telegram_bot.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.bot.AbstractTelegramBot;
import mavmi.telegram_bot.common.dto.impl.water_stuff.water_stuff_service.WaterStuffServiceRs;
import mavmi.telegram_bot.water_stuff.telegram_bot.httpClient.HttpClient;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.util.List;

@Slf4j
@Component
public class Bot extends AbstractTelegramBot {

    private final HttpClient httpClient;

    public Bot(
            HttpClient httpClient,
            @Value("${telegram-bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.httpClient = httpClient;
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
                    Response response = httpClient.waterStuffServiceRequest(update.message());

                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        WaterStuffServiceRs waterStuffServiceRs = objectMapper.readValue(response.body().string(), WaterStuffServiceRs.class);

                        switch (waterStuffServiceRs.getWaterStuffServiceTask()) {
                            case SEND_TEXT -> sendText(chatId, waterStuffServiceRs.getMessageJson().getTextMessage());
                            case SEND_KEYBOARD -> sendKeyboard(
                                    chatId,
                                    waterStuffServiceRs.getMessageJson().getTextMessage(),
                                    waterStuffServiceRs.getKeyboardJson().getKeyboardButtons()
                            );
                        }
                    } else {
                        Bot.this.sendMessage(chatId, "Service unavailable");
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
