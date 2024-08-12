package mavmi.telegram_bot.rocketchat.telegramBot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketchatService;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class RocketchatTelegramBot extends TelegramBot {

    private final RocketchatService rocketchatService;
    private final RequestsMapper requestsMapper;

    public RocketchatTelegramBot(
            RocketchatService rocketchatService,
            RequestsMapper requestsMapper,
            @Value("${telegram-bot.token}") String telegramBotToken
    ) {
        super(telegramBotToken);
        this.rocketchatService = rocketchatService;
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
                    long chatId = message.chat().id();
                    log.info("Got request from id {}", chatId);
                    RocketchatServiceRq rocketchatServiceRq = requestsMapper.telegramRequestToRocketchatServiceRequest(message);

                    CompletableFuture
                            .supplyAsync(() -> {
                                return rocketchatService.handleRequest(rocketchatServiceRq);
                            }).thenApply(arg -> {
                                RocketchatServiceRs rocketchatServiceRs = (RocketchatServiceRs) arg;
                                if (rocketchatServiceRs == null) {
                                    return null;
                                }

                                switch (rocketchatServiceRs.getRocketchatServiceTask()) {
                                    case SEND_TEXT -> sendTextMessage(chatId, rocketchatServiceRs.getMessageJson().getTextMessage());
                                    case SEND_IMAGE -> {
                                        File file = new File(rocketchatServiceRs.getImageJson().getFilePath());
                                        String textMessage = rocketchatServiceRs.getMessageJson().getTextMessage();
                                        sendImage(chatId, file, textMessage);
                                        file.delete();
                                    }
                                }

                                return rocketchatServiceRs;
                            }).join();
                }

                return UpdatesListener.CONFIRMED_UPDATES_ALL;
            }
        }, e -> {
            e.printStackTrace(System.out);
        });
    }
}
