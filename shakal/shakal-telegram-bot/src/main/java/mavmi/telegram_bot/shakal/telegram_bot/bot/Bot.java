package mavmi.telegram_bot.shakal.telegram_bot.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.bot.AbstractTelegramBot;
import mavmi.telegram_bot.common.dto.common.DiceJson;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRq;
import mavmi.telegram_bot.common.dto.impl.shakal.service.ShakalServiceRs;
import mavmi.telegram_bot.common.httpFilter.UserSessionHttpFilter;
import mavmi.telegram_bot.shakal.telegram_bot.httpClient.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;

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
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                log.info("Got request from id {}", update.message().from().id());

                long chatId = update.message().from().id();
                ResponseEntity<ShakalServiceRs> response = httpClient.shakalServiceRequest(
                        update.message(),
                        update.message().from(),
                        update.message().dice()
                );

                if (response.getStatusCode().equals(HttpStatusCode.valueOf(HttpStatus.OK.value()))) {
                    ShakalServiceRs shakalServiceRs = response.getBody();

                    switch (shakalServiceRs.getShakalServiceTask()) {
                        case SEND_TEXT -> sendText(chatId, shakalServiceRs);
                        case SEND_KEYBOARD -> sendKeyboard(chatId, shakalServiceRs);
                        case SEND_DICE -> sendDice(chatId, shakalServiceRs);
                    }
                } else {
                    Bot.this.sendMessage(chatId, "Service unavailable");
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            e.printStackTrace(System.out);
        });
    }

    public void sendText(long chatId, ShakalServiceRs shakalServiceRs) {
        sendMessage(
                chatId,
                shakalServiceRs.getMessageJson().getTextMessage(),
                ParseMode.Markdown
        );
    }

    public void sendKeyboard(long chatId, ShakalServiceRs shakalServiceRs) {
        String msg = shakalServiceRs.getMessageJson().getTextMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : shakalServiceRs.getKeyboardJson().getKeyboardButtons()) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));
    }

    @SneakyThrows
    public void sendDice(long chatId, ShakalServiceRs shakalServiceRs) {
        String msg = shakalServiceRs.getMessageJson().getTextMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : shakalServiceRs.getKeyboardJson().getKeyboardButtons()) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));

        int botDiceValue = sendRequest(new SendDice(chatId)).message().dice().value();
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
        httpClient.sendPostRequest(
                httpClient.serviceUrl,
                httpClient.shakaServiceRequestEndpoint,
                Map.of(UserSessionHttpFilter.ID_HEADER_NAME, String.valueOf(chatId)),
                requestBody,
                ShakalServiceRs.class
        );
    }
}
