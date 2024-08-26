package mavmi.telegram_bot.shakal.telegramBot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendDice;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.cache.impl.CacheContainer;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.shakal.cache.ShakalServiceDataCache;
import mavmi.telegram_bot.shakal.mapper.RequestsMapper;
import mavmi.telegram_bot.shakal.service.ShakalDirectService;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRq;
import mavmi.telegram_bot.shakal.service.dto.ShakalServiceRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ShakalTelegramBot extends TelegramBot {

    private final RequestsMapper requestsMapper;
    private final CacheContainer cacheContainer;
    private final ShakalDirectService shakalService;

    public ShakalTelegramBot(
            RequestsMapper requestsMapper,
            CacheContainer cacheContainer,
            ShakalDirectService shakalService,
            @Value("${telegram-bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.requestsMapper = requestsMapper;
        this.cacheContainer = cacheContainer;
        this.shakalService = shakalService;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                log.info("Got request from id {}", update.message().from().id());

                long chatId = update.message().from().id();
                ShakalServiceRq shakalServiceRq = requestsMapper.telegramRequestToShakalServiceRequest(update.message());
                log.info(shakalServiceRq.toString());

                CompletableFuture
                        .supplyAsync(() -> {
                            return shakalService.handleRequest(shakalServiceRq);
                        }).thenApply(arg -> {
                            ShakalServiceRs shakalServiceRs = (ShakalServiceRs) arg;
                            if (shakalServiceRs == null) {
                                return null;
                            }

                            switch (shakalServiceRs.getShakalServiceTask()) {
                                case SEND_TEXT -> sendText(chatId, shakalServiceRs);
                                case SEND_TEXT_DELETE_KEYBOARD -> sendTextMessage(chatId, shakalServiceRs.getMessageJson().getTextMessage(), ParseMode.Markdown, new ReplyKeyboardRemove());
                                case SEND_KEYBOARD -> sendReplyKeyboard(chatId, shakalServiceRs);
                                case SEND_DICE -> sendDice(chatId, shakalServiceRs);
                            }
                            return shakalServiceRs;
                        }).join();
            }

            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, e -> {
            log.error(e.getMessage(), e);
        });
    }

    public void sendText(long chatId, ShakalServiceRs shakalServiceRs) {
        sendTextMessage(
                chatId,
                shakalServiceRs.getMessageJson().getTextMessage(),
                ParseMode.Markdown
        );
    }

    public void sendReplyKeyboard(long chatId, ShakalServiceRs shakalServiceRs) {
        String msg = shakalServiceRs.getMessageJson().getTextMessage();
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup(new String[]{})
                .resizeKeyboard(true)
                .oneTimeKeyboard(true);
        for (String button : shakalServiceRs.getReplyKeyboardJson().getKeyboardButtons()) {
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
        for (String button : shakalServiceRs.getReplyKeyboardJson().getKeyboardButtons()) {
            replyKeyboardMarkup.addRow(button);
        }

        sendMessage(new SendMessage(chatId, msg).replyMarkup(replyKeyboardMarkup));

        int botDiceValue = sendRequest(new SendDice(chatId)).message().dice().value();
        cacheContainer.getDataCacheByUserId(chatId, ShakalServiceDataCache.class).setBotDice(botDiceValue);
    }
}
