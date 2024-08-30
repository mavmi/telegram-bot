package mavmi.telegram_bot.rocketchat.telegramBot.userThread;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mavmi.telegram_bot.common.service.dto.common.DeleteMessageJson;
import mavmi.telegram_bot.common.service.dto.common.tasks.ROCKETCHAT_SERVICE_TASK;
import mavmi.telegram_bot.common.service.method.chained.ChainedServiceModuleSecondaryMethod;
import mavmi.telegram_bot.common.telegramBot.userThread.UserThread;
import mavmi.telegram_bot.rocketchat.cache.inner.dataCache.RocketchatServiceDataCacheMessagesIdsHistory;
import mavmi.telegram_bot.rocketchat.mapper.RequestsMapper;
import mavmi.telegram_bot.rocketchat.service.RocketchatService;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRq;
import mavmi.telegram_bot.rocketchat.service.dto.rocketchatService.RocketchatServiceRs;
import mavmi.telegram_bot.rocketchat.telegramBot.RocketchatTelegramBotSender;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
public class RocketchatUserThread implements UserThread {

    private final RocketchatUserThreads userThreads;
    private final RocketchatTelegramBotSender sender;
    private final RocketchatService rocketchatService;
    private final RequestsMapper requestsMapper;
    private final long chatId;
    private final Queue<Update> updateQueue = new ArrayDeque<>();
    private final Deque<Integer> messagesIdsHistory = new ArrayDeque<>();

    @Override
    public void add(Update update) {
        updateQueue.add(update);
    }

    @Override
    @SneakyThrows
    public void run() {
        while (!updateQueue.isEmpty()) {
            boolean deleteBeforeNext = false;
            Message message = updateQueue.remove().message();
            RocketchatServiceRq rocketchatServiceRq = requestsMapper.telegramRequestToRocketchatServiceRequest(message);
            List<ChainedServiceModuleSecondaryMethod<RocketchatServiceRs, RocketchatServiceRq>> methods = rocketchatService.prepareMethodsChain(rocketchatServiceRq);

            for (var method : methods) {
                RocketchatServiceRs rocketchatServiceRs = rocketchatService.handleRequest(rocketchatServiceRq, method);
                if (rocketchatServiceRs == null) {
                    continue;
                }

                for (ROCKETCHAT_SERVICE_TASK task : rocketchatServiceRs.getRocketchatServiceTasks()) {
                    if (deleteBeforeNext && !messagesIdsHistory.isEmpty()) {
                        deleteBeforeNext = false;
                        sender.deleteMessage(chatId, messagesIdsHistory.removeLast());
                    }

                    switch (task) {
                        case SEND_TEXT -> sendText(rocketchatServiceRs);
                        case SEND_IMAGE -> sendImage(rocketchatServiceRs);
                        case DELETE -> delete(rocketchatServiceRs);
                        case DELETE_AFTER_TIME_MILLIS -> deleteAfterMillis(rocketchatServiceRs);
                        case DELETE_BEFORE_NEXT_MESSAGE -> deleteBeforeNext = true;
                        case DELETE_AFTER_END -> deleteAfterEnd(rocketchatServiceRs);
                        case END -> end();
                    }
                }
            }

            messagesIdsHistory.clear();
        }

        userThreads.removeThread(chatId);
    }

    private void sendText(RocketchatServiceRs response) {
        SendResponse sendResponse = sender.sendTextMessage(chatId, response.getMessageJson().getTextMessage());
        messagesIdsHistory.add(sendResponse.message().messageId());
    }

    private void sendImage(RocketchatServiceRs response) {
        File file = new File(response.getImageJson().getFilePath());
        String textMessage = response.getMessageJson().getTextMessage();
        SendResponse sendResponse = sender.sendImage(chatId, file, textMessage);
        messagesIdsHistory.add(sendResponse.message().messageId());
        file.delete();
    }

    private void delete(RocketchatServiceRs response) {
        sender.deleteMessage(chatId, response.getDeleteMessageJson().getMsgId());
    }

    @SneakyThrows
    private void deleteAfterMillis(RocketchatServiceRs response) {
        DeleteMessageJson deleteMessageJson = response.getDeleteMessageJson();
        Integer msgId = deleteMessageJson.getMsgId();
        long millis = deleteMessageJson.getDeleteAfterMillis();
        Thread.sleep(millis);

        int msgIdToDelete;
        if (msgId == null) {
            if (!messagesIdsHistory.isEmpty()) {
                msgIdToDelete = messagesIdsHistory.removeLast();
            } else {
                return;
            }
        } else {
            msgIdToDelete = msgId;
        }

        sender.deleteMessage(chatId, msgIdToDelete);
    }

    private void deleteAfterEnd(RocketchatServiceRs response) {
        Integer msgId = response.getDeleteMessageJson().getMsgId();
        int msgIdToDelete = (msgId == null) ? messagesIdsHistory.removeLast() : msgId;
        Long commandCache = rocketchatService.getActiveCommandCache();
        rocketchatService.getMessagesIdsHistory().add(commandCache, msgIdToDelete);
    }

    private void end() {
        RocketchatServiceDataCacheMessagesIdsHistory history = rocketchatService.getMessagesIdsHistory();
        Long commandCache = rocketchatService.getActiveCommandCache();
        while (history.size(commandCache) > 0) {
            sender.deleteMessage(chatId, history.removeLast(commandCache));
        }
    }
}
