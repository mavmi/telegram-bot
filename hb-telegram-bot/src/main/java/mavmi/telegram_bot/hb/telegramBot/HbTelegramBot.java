package mavmi.telegram_bot.hb.telegramBot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.ParseMode;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.hb.mapper.RequestsMapper;
import mavmi.telegram_bot.hb.service.HbService;
import mavmi.telegram_bot.hb.service.dto.HbServiceRequest;
import mavmi.telegram_bot.hb.service.dto.HbServiceResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class HbTelegramBot extends TelegramBot {

    private final HbService hbService;
    private final RequestsMapper requestsMapper;

    public HbTelegramBot(
            HbService hbService,
            RequestsMapper requestsMapper,
            @Value("${telegram-bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.hbService = hbService;
        this.requestsMapper = requestsMapper;
    }

    @Override
    @PostConstruct
    public void run() {
        telegramBot.setUpdatesListener(new UpdatesListener() {
            @Override
            @SneakyThrows
            public int process(List<Update> updates) {
                for (Update update : updates) {
                    Message message = update.message();
                    if (message == null || message.text() == null) {
                        log.info("Message is null");
                        continue;
                    }

                    long chatId = message.chat().id();
                    log.info("Got request from id {}", chatId);
                    HbServiceRequest hbServiceRequest = requestsMapper.telegramRequestToHbServiceRequest(message);

                    new Thread(new Runnable() {
                        @Override
                        @SneakyThrows
                        public void run() {
                            HbServiceResponse hbServiceResponse = hbService.handleRequest(hbServiceRequest);

                            switch (hbServiceResponse.getHbServiceTask()) {
                                case SEND_TEXT -> {
                                    String msg = hbServiceResponse.getMessageJson().getTextMessage();

                                    sendRequest(new SendMessage(chatId, msg)
                                            .parseMode(ParseMode.Markdown)
                                            .replyMarkup(new ReplyKeyboardRemove()));
                                }
                                case SEND_KEYBOARD -> {
                                    String msg = hbServiceResponse.getMessageJson().getTextMessage();

                                    ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup(new String[]{})
                                            .resizeKeyboard(true)
                                            .oneTimeKeyboard(true);
                                    for (String buttonStr : hbServiceResponse.getReplyKeyboardJson().getKeyboardButtons()){
                                        keyboard.addRow(buttonStr);
                                    }

                                    sendRequest(new SendMessage(chatId, msg).replyMarkup(keyboard));
                                }
                            }
                        }
                    }).start();
                }

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
            e.printStackTrace(System.out);
        });
    }
}
