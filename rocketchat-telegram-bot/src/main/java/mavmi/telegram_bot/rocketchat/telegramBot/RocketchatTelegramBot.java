package mavmi.telegram_bot.rocketchat.telegramBot;

import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import mavmi.telegram_bot.common.service.dto.common.DeleteMessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.telegramBot.TelegramBot;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketchatService;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

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
                    if (message == null) {
                        log.info("Message is null");
                        continue;
                    }

                    long chatId = message.chat().id();
                    log.info("Got request from id {}", chatId);
                    RocketchatServiceRq rocketchatServiceRq = requestsMapper.telegramRequestToRocketchatServiceRequest(message);

                    new Thread(new Runnable() {
                        @Override
                        @SneakyThrows
                        public void run() {
                            boolean deleteBeforeNext = false;
                            Deque<Integer> messagesIdsHistory = new ArrayDeque<>();
                            List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methods = rocketchatService.prepareMethodsChain(rocketchatServiceRq);

                            for (var method : methods) {
                                RocketchatServiceRs rocketchatServiceRs = rocketchatService.handleRequest(rocketchatServiceRq, method);
                                if (rocketchatServiceRs == null) {
                                    continue;
                                }

                                for (ROCKETCHAT_SERVICE_TASK task : rocketchatServiceRs.getRocketchatServiceTasks()) {
                                    if (deleteBeforeNext && !messagesIdsHistory.isEmpty()) {
                                        deleteBeforeNext = false;
                                        deleteMessage(chatId, messagesIdsHistory.removeLast());
                                    }

                                    switch (task) {
                                        case SEND_TEXT -> {
                                            SendResponse sendResponse = sendTextMessage(chatId, rocketchatServiceRs.getMessageJson().getTextMessage());
                                            messagesIdsHistory.add(sendResponse.message().messageId());
                                        }
                                        case SEND_IMAGE -> {
                                            File file = new File(rocketchatServiceRs.getImageJson().getFilePath());
                                            String textMessage = rocketchatServiceRs.getMessageJson().getTextMessage();
                                            SendResponse sendResponse = sendImage(chatId, file, textMessage);
                                            messagesIdsHistory.add(sendResponse.message().messageId());
                                            file.delete();
                                        }
                                        case DELETE -> {
                                            deleteMessage(chatId, rocketchatServiceRs.getDeleteMessageJson().getMsgId());
                                        }
                                        case DELETE_AFTER_MILLIS -> {
                                            DeleteMessageJson deleteMessageJson = rocketchatServiceRs.getDeleteMessageJson();
                                            Integer msgId = deleteMessageJson.getMsgId();
                                            long millis = deleteMessageJson.getDeleteAfterMillis();
                                            Thread.sleep(millis);

                                            int msgIdToDelete;
                                            if (msgId == null) {
                                                if (!messagesIdsHistory.isEmpty()) {
                                                    msgIdToDelete = messagesIdsHistory.removeLast();
                                                } else {
                                                    continue;
                                                }
                                            } else {
                                                msgIdToDelete = msgId;
                                            }

                                            deleteMessage(chatId, msgIdToDelete);
                                        }
                                        case DELETE_BEFORE_NEXT -> {
                                            deleteBeforeNext = true;
                                        }
                                    }
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
